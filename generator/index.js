const amqp = require('amqplib');
const mongo = require('mongodb').MongoClient;
const Zip = require('jszip');
const fs = require('fs');
const Document = require('docxtemplater');
const expressions = require('angular-expressions');

const angularParser = function (tag) {
    return {
        get: tag === '.' ? function (s) { return s; } : function (s) {
            return expressions.compile(tag.replace(/(’|“|”)/g, "'"))(s);
        }
    };
}

const channelPromise = amqp.connect(process.env.QUEUE_URL).then(connection => connection.createChannel());
const databasePromise = mongo.connect(process.env.DATABASE_URL, { useNewUrlParser: true })
    .then(client => {
        const collection = client.db().collection('documents');
        return collection.createIndex("date", { expireAfterSeconds: 300 })
            .then(() => collection);
    });

function loadTemplate(template) {
    return new Promise(function (resolve, reject) {
        fs.readFile(template + ".docx", "binary", function (err, data) {
            if (err) {
                reject(err);
            } else {
                resolve(data);
            }
        })
    });
}

function renderTemplate(raw, data) {
    var doc = new Document();
    doc.loadZip(new Zip(raw));
    doc.setOptions({ parser: angularParser });
    doc.setData(data);
    doc.render();
    return doc.getZip().generate({ type: 'nodebuffer', compression: 'DEFLATE' });
}

function saveDocument(id, fileName, data) {
    return databasePromise.then(collection => collection.insertOne({ jobId: id, fileName: fileName, data: data, date: new Date() }));
}

function sendResponse(jobId, documentId, success) {
    return channelPromise.then(channel => channel.assertQueue('results').then(() => {
        const message = JSON.stringify({ jobId: jobId, documentId: documentId, success: success });
        console.log("Sending response for request: " + message + ".");
        const buffer = Buffer.from(message);
        return channel.sendToQueue('results', buffer);
    }));
}

function generateDocument(id, template, fileName, data) {
    return loadTemplate(template)
        .then(raw => renderTemplate(raw, data))
        .then(out => saveDocument(id, fileName, out))
        .then(doc => sendResponse(id, doc.insertedId, true), () => sendResponse(id, null, false))
        .catch(err => console.log("Got error while generating document: " + err))
}

channelPromise.then(channel => channel.assertQueue('documents').then(() => channel.consume('documents', msg => {
    const parsed = JSON.parse(msg.content.toString());
    generateDocument(parsed.jobId, parsed.template, parsed.fileName, parsed.data);
    channel.ack(msg);
})));
