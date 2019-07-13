// global m
const person = require("../models").person;
const scroll = require("../util/scroll");
const loader = require("../components/loader");
const address = require("../components/address");

const onScroll = scroll(person.page.load);

const list = {
    oninit() {
        window.addEventListener("scroll", onScroll);
        return person.page.init();
    },
    onremove() {
        window.removeEventListener("scroll", onScroll);
    },
    view: () => m("div", [
        m("a.ui.button.positive.icon.float-right", { href: "/persons/new", oncreate: m.route.link }, m("i.icon.plus")),
        m("h3.ui.header.header-with-button", "Persons"),
        m("div.ui.middle.aligned.selection.list.animated.divided.large", person.page.content
            .map(p => m("div.item", m("div.content", m("a.header", { href: "/persons/" + p.id, oncreate: m.route.link }, p.name))))
        )
    ])
};

const view = {
    oninit: node => person.current.load(node.attrs.id),
    view() {
        if (person.current.loading) {
            return m(loader);
        }
        const data = person.current.data;
        return m("form.ui.form", [
            m("a.ui.button.primary.icon.float-right", { href: "/persons/" + data.id + "/edit", oncreate: m.route.link },
                m("i.icon.edit")),
            m("h3.ui.header.header-with-button", "Person: " + data.name),
            m("h4.ui.dividing.header", "General Details"),
            m("div.two.fields", [
                m("div.field", [m("label", "Name"), m("div.field", data.name)]),
                m("div.field", [m("label", "Personal Number"), m("div.field", data.personalNumber)]),
            ]),
            m("h4.ui.dividing.header", "Address"),
            m(address.view, data.address)
        ]);
    }
};

const editable = {
    oninit: node => node.attrs.onInit(),
    view(node) {
        if (person.current.loading) {
            return m(loader);
        }
        const data = person.current.data;
        return m("form.ui.form", [
            m("a.ui.button.positive.icon.float-right", {
                onclick: () => node.attrs.onSave().then(data => m.route.set("/persons/" + data.id)),
            }, m("i.icon.save")),
            m("a.ui.button.negative.icon.float-right", { href: node.attrs.onCancelHref, oncreate: m.route.link },
                m("i.icon.cancel")),
            m("h3.ui.header.header-with-button", node.attrs.title || ("Person: " + data.name)),
            m("h4.ui.dividing.header", "General Details"),
            m("div.two.fields", [
                m("div.field", [m("label", "Name"), m("div.field", m("input", {
                    type: "text", placeholder: "Name",
                    value: data.name, oninput: e => data.name = e.target.value
                }))]),
                m("div.field", [m("label", "Personal Number"), m("div.field", m("input", {
                    type: "text", placeholder: "Personal Number",
                    value: data.personalNumber, oninput: e => data.personalNumber = e.target.value
                }))]),
            ]),
            m("h4.ui.dividing.header", "Address"),
            m(address.edit, { data: data.address, onChange: (p, v) => data.address[p] = v })
        ]);
    }
};


const edit = {
    view: node => m(editable, {
        id: node.attrs.id,
        onSave: () => person.current.update(),
        onInit: () => person.current.load(node.attrs.id),
        onCancelHref: "/persons/" + node.attrs.id
    })
};

const create = {
    view: () => m(editable, {
        onSave: () => person.current.create(),
        onInit: () => person.current.init(),
        onCancelHref: "/persons",
        title: "New Person"
    })
};

module.exports = {
    list: list,
    view: view,
    edit: edit,
    create: create
};