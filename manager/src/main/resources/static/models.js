// global m

function paramsToUrl(params) {
    let result = [];
    for (const key in params) {
        if (params.hasOwnProperty(key)) {
            result.push(key + "=" + encodeURIComponent(params[key]));
        }
    }
    return result.join("&")
}

function PageModel(getBaseUrl, sort) {
    this.size = 20;
    this.page = 0;
    this.done = false;
    this.content = [];
    this.filter = {};
    this.getBaseUrl = getBaseUrl;

    this.init = filter => {
        this.content = [];
        this.page = 0;
        this.done = false;
        this.filter = filter;
        return this.load();
    };

    this.load = () => {
        if (this.done) {
            return Promise.resolve();
        } else {
            const params = Object.assign({ sort: sort, page: this.page, size: this.size }, this.filter);
            return m.request({
                method: "GET",
                url: this.getBaseUrl() + "?" + paramsToUrl(params),
                withCredentials: true,
            }).then(result => {
                this.content = this.content.concat(result.content);
                this.page++;
                this.done = !result.content.length;
                return result;
            })
        }
    };
}

function CurrentModel(getBaseUrl, initialDataSupplier) {
    this.data = {};
    this.loading = false;
    this.getBaseUrl = getBaseUrl

    this.init = (input) => {
        this.loading = false;
        this.data = initialDataSupplier(input);
    };

    this.load = id => {
        this.loading = true;
        return m.request({
            method: "GET",
            url: this.getBaseUrl() + "/" + id,
            withCredentials: true,
        }).then(result => {
            this.loading = false;
            this.data = result;
            return result;
        });
    };

    this.update = () => {
        this.loading = true;
        return m.request({
            method: "PUT",
            url: this.getBaseUrl() + "/" + this.data.id,
            data: this.data,
            withCredentials: true,
        }).then(result => {
            this.loading = false;
            this.data = result;
            return result;
        });
    };

    this.create = () => {
        this.loading = true;
        return m.request({
            method: "POST",
            url: this.getBaseUrl(),
            data: this.data,
            withCredentials: true,
        }).then(result => {
            this.loading = false;
            this.data = result;
            return result;
        });
    }
}

function BaseModel(baseUrl, pageSort, initialDataSupplier) {
    this.page = new PageModel(() => baseUrl, pageSort);
    this.current = new CurrentModel(() => baseUrl, initialDataSupplier);
}

function DownloadModel() {
    this.loading = false;

    const trigger = (triggerUrl, data) => {
        this.loading = true;
        return m.request({
            method: "POST",
            url: triggerUrl,
            data: data || {},
            withCredentials: true,
        }).then(result => {
            this.loading = false;
            return result.id;
        });
    }

    const queryStatus = jobId => m.request({
        method: "GET",
        url: "/jobs/" + jobId,
        withCredentials: true,
    }).then(result => result.status)

    const waitForFinish = jobId => new Promise((resolve, reject) => {
        let interval;
        const cleanup = () => {
            clearInterval(interval);
            this.loading = false;
        }
        const check = () => queryStatus(jobId).then(status => {
            if (status === "FINISHED") {
                cleanup();
                resolve(jobId);
            } else if (status === "ERROR") {
                cleanup();
                reject({message: "Unable to generate document."});
            }
        });
        interval = setInterval(check, 1000);
    });

    this.getDownloadUrl = (triggerUrl, data) => trigger(triggerUrl, data)
        .then(waitForFinish)
        .then(jobId => "/jobs/" + jobId + "/download");
}

function initialPersonSupplier() {
    return {
        name: "",
        personalNumber: "",
        address: {
            country: "",
            region: "",
            streetAddress: "",
            postalCode: ""
        }
    };
}

function initialCompanySupplier(managed) {
    return {
        name: "",
        vat: false,
        capital: 0,
        email: "",
        managed: managed,
        bankAccount: {
            bank: "",
            iban: ""
        },
        identification: {
            number: "",
            registration: ""
        },
        headquarters: {
            country: "",
            region: "",
            streetAddress: "",
            postalCode: ""
        },
        office: {
            country: "",
            region: "",
            streetAddress: "",
            postalCode: ""
        }
    };
}

const twoPad = num => num >= 10 ? (num.toString()) : ("0" + num);
const dateToString = date => date.getFullYear() + "-" + twoPad(date.getMonth() + 1) + "-" + twoPad(date.getDate());

function InvoiceModel() {
    this.company = null;
    const getBaseUrl = () => "/companies/" + this.company + "/invoices";

    const supplier = () => {
        const now = new Date();
        return {
            series: "",
            number: "",
            issueDate: dateToString(now),
            dueDate: dateToString(now),
            company: { id: this.company, name: "" },
            customer: { id: null, name: "" },
            delegate: { id: null, name: "" },
            lines: []
        };
    }

    this.page = new PageModel(getBaseUrl, "id,desc");
    this.current = new CurrentModel(getBaseUrl, supplier);
    this.setCompany = c => this.company = c;
    this.download = new DownloadModel();
    this.download.getDownloadUrlForInvoice = (c, i) => this.download.getDownloadUrl("/companies/" + c + "/invoices/" + i + "/generate");
    this.batch = new DownloadModel();
    this.batch.getDownloadUrlForIds = ids => this.batch.getDownloadUrl("/invoices/generate-batch", ids);
}

module.exports = {
    person: new BaseModel("/persons", "name", initialPersonSupplier),
    company: new BaseModel("/companies", "name", initialCompanySupplier),
    invoice: new InvoiceModel()
};