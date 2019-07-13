// global m
const invoice = require("../models").invoice;
const scroll = require("../util/scroll");
const loader = require("../components/loader");

const onScroll = scroll(invoice.page.load);
const isHeaderPresent = header => header !== null && header.id !== null;
const companyLinkFactory = header => m("a", { href: "/companies/" + header.id, oncreate: m.route.link }, header.name);
const personLinkFactory = header => m("a", { href: "/persons/" + header.id, oncreate: m.route.link }, header.name);

const list = {
    oninit(node) {
        window.addEventListener("scroll", onScroll);
        invoice.setCompany(node.attrs.company);
        return invoice.page.init();
    },

    onupdate(node) {
        if (node.attrs.company !== invoice.company) {
            invoice.setCompany(node.attrs.company);
            invoice.page.init();
        }
    },

    onremove() {
        window.removeEventListener("scroll", onScroll);
    },

    view(node) {
        const company = node.attrs.company;
        const content = invoice.page.content;
        const selection = content.filter(i => i.selected).map(i => i.id);
        const onClickFactory = id => () => m.route.set("/companies/" + company + "/invoices/" + id);
        const onDownload = e => {
            if (!invoice.batch.loading && selection.length) {
                invoice.batch.getDownloadUrlForIds(selection).then(url => window.location.assign(url));
                m.redraw();
            }
            e.preventDefault();
        };
        return m("div", [
            m("a.ui.button.positive.icon.float-right", {href: "/companies/" + company + "/invoices/new", oncreate: m.route.link }, 
                m("i.icon.plus")),
            m("a.ui.button.icon.float-right" + (invoice.batch.loading ? ".loading" : "") + (!selection.length ? ".disabled" : ""), 
                { onclick: onDownload }, m("i.icon.download")),
            m("h3.ui.header.header-with-button", "Invoices"),
            m("table.ui.very.basic.table.selectable ",
                m("thead", m("tr", [m("th", "Issue Date"), m("th", "Number"), m("th", "Amount"), m("th")])),
                m("tbody", content.map(invoice => m("tr", [
                    m("td", { onclick: onClickFactory(invoice.id) }, invoice.issueDate),
                    m("td", { onclick: onClickFactory(invoice.id) }, invoice.series + "/" + invoice.number),
                    m("td", { onclick: onClickFactory(invoice.id) }, invoice.total),
                    m("td", { onclick: () => invoice.selected = !invoice.selected }, 
                        m("div.ui.checkbox", { oncreate: node => $(node.dom).checkbox() }, [
                            m("input.hidden", {  type: "checkbox", checked: invoice.selected || false }),
                            m("label")
                        ])
                    )
                ])))
            )
        ]);
    }
};

const view = {
    oninit(node) {
        invoice.setCompany(node.attrs.company);
        return invoice.current.load(node.attrs.id);
    },

    onupdate(node) {
        if (!invoice.current.loading && (node.attrs.company != invoice.company || node.attrs.id != invoice.current.data.id)) {
            invoice.setCompany(node.attrs.company);
            invoice.current.load(node.attrs.id);
        }
    },

    view() {
        if (invoice.current.loading) {
            return m(loader);
        }
        const data = invoice.current.data;
        const editLink = "/companies/" + invoice.company + "/invoices/" + data.id + "/edit";
        const onDownload = e => {
            if (!invoice.download.loading) {
                invoice.download.getDownloadUrlForInvoice(invoice.company, data.id)
                    .then(url => window.location.assign(url));
            }
            e.preventDefault();
        };
        return m("form.ui.form", [
            m("a.ui.button.primary.icon.float-right", { href: editLink, oncreate: m.route.link }, m("i.icon.edit")),
            m("a.ui.button.icon.float-right" + (invoice.download.loading ? ".loading" : ""), { onclick: onDownload }, m("i.icon.download")),
            m("h3.ui.header.header-with-button", "Invoice: " + data.series + "/" + data.number),
            m("div.field", [m("label", "Company"), m("div.field", companyLinkFactory(data.company))]),
            m("div.two.fields", [
                m("div.field", [m("label", "Series"), m("div.field", data.series)]),
                m("div.field", [m("label", "Number"), m("div.field", data.number)]),
            ]),
            m("div.two.fields", [
                m("div.field", [m("label", "Issue Date"), m("div.field", data.issueDate)]),
                m("div.field", [m("label", "Due Date"), m("div.field", data.dueDate)]),
            ]),
            m("div.two.fields", [
                m("div.field", [m("label", "Customer"), m("div.field", companyLinkFactory(data.customer))]),
                m("div.field", [m("label", "Delegate"), m("div.field", isHeaderPresent(data.delegate) ?
                    personLinkFactory(data.delegate) : "none")]),
            ]),
            m("h4.ui.dividing.header", "Items"),
            m("table.ui.very.basic.table ",
                m("thead", m("tr", [m("th", "Name"), m("th", "Quantity"), m("th", "VAT Rate"), m("th", "Unit Price (excl. VAT)")])),
                m("tbody", data.lines.map(line => m("tr", [
                    m("td", line.name),
                    m("td", line.quantity),
                    m("td", line.vatRate * 100 + "%"),
                    m("td", line.unitPrice)
                ])))
            )
        ]);
    }
};

const editable = {
    oninit(node) {
        invoice.setCompany(node.attrs.company);
        return node.attrs.onInit();
    },

    view(node) {
        if (invoice.current.loading) {
            return m(loader);
        }
        const data = invoice.current.data;
        const company = node.attrs.company;
        const searchSetupFactory = (property, baseUrl) => node => $(node.dom).search({
            apiSettings: { url: baseUrl + '?name={query}' },
            fields: { title: 'name' },
            onSelect: result => {
                data[property] = result;
                m.redraw();
            }
        });
        return m("form.ui.form", [
            m("a.ui.button.positive.icon.float-right", {
                onclick: () => node.attrs.onSave().then(data => m.route.set("/companies/" + company + "/invoices/" + data.id)),
            }, m("i.icon.save")),
            m("a.ui.button.negative.icon.float-right", { href: node.attrs.onCancelHref, oncreate: m.route.link }, m("i.icon.cancel")),
            m("h3.ui.header.header-with-button", node.attrs.title || ("Invoice: " + data.series + "/" + data.number)),
            m("div.field", [m("label", "Company"), m("div.field", companyLinkFactory(data.company))]),
            m("div.two.fields", [
                m("div.field", [m("label", "Series"), m("div.field", m("input", {
                    type: "text", placeholder: "Series", value: data.series,
                    oninput: e => data.series = e.target.value
                }))]),
                m("div.field", [m("label", "Number"), m("div.field", m("input", {
                    type: "text", placeholder: "Number", value: data.number,
                    oninput: e => data.number = e.target.value
                }))]),
            ]),
            m("div.two.fields", [
                m("div.field", [m("label", "Issue Date"), m("div.field", m("input", {
                    type: "text", placeholder: "Issue Date", value: data.issueDate,
                    oninput: e => data.issueDate = e.target.value
                }))]),
                m("div.field", [m("label", "Due Date"), m("div.field", m("input", {
                    type: "text", placeholder: "Issue Date", value: data.dueDate,
                    oninput: e => data.dueDate = e.target.value
                }))]),
            ]),
            m("div.field", [
                m("label", "Customer"),
                m("div.two.fields", [
                    m("div.field", m("div.ui.label", data.customer && data.customer.id ?
                        data.customer.name : m("em", "none"))),
                    m("div.field", m("div.ui.search", { oncreate: searchSetupFactory("customer", "/company-suggestions") },
                        [m("input.prompt", { type: "text", placeholder: "Search customers..." }), m("div.results")])),
                ])
            ]),
            m("div.field", [
                m("label", "Delegate"),
                m("div.two.fields", [
                    m("div.field", data.delegate && data.delegate.id ?
                        m("div.ui.label", [data.delegate.name, m("i.icon.delete", { onclick: () => data.delegate = null })]) :
                        m("div.ui.label.basic", m("em", "without delegate..."))),
                    m("div.field", m("div.ui.search", { oncreate: searchSetupFactory("delegate", "/person-suggestions") },
                        [m("input.prompt", { type: "text", placeholder: "Search people..." }), m("div.results")]))
                ])
            ]),
            m("h4.ui.dividing.header", "Items"),
            m("table.ui.very.basic.table ",
                m("thead", m("tr", [m("th", "Name"), m("th", "Quantity"), m("th", "VAT Rate"), m("th", "Unit Price (excl. VAT)"), m("th")])),
                m("tbody", data.lines.map((line, index) => m("tr", [
                    m("td", m("input", {
                        type: "text", placeholder: "Name", value: line.name,
                        oninput: e => line.name = e.target.value
                    })),
                    m("td", m("input", {
                        type: "number", placeholder: "Quantity", step: "0.01", value: line.quantity,
                        oninput: e => line.quantity = parseFloat(e.target.value)
                    })),
                    m("td", m("div.ui.right.labeled.input", [m("input", {
                        type: "number", placeholder: "Vat Rate", step: "0.01", value: line.vatRate * 100,
                        oninput: e => line.vatRate = parseFloat(e.target.value) / 100
                    }), m("div.ui.basic.label", "%")])),
                    m("td", m("input", {
                        type: "number", placeholder: "Unit Price", step: "0.01", value: line.unitPrice,
                        oninput: e => line.unitPrice = parseFloat(e.target.value)
                    })),
                    m("td", m("i.icon.link.red.delete", { onclick: () => data.lines.splice(index, 1) }))
                ]))),
                m("tfoot", m("tr", m("th", { colspan: 5 }, m("a.ui.button.positive.float-right",
                    { onclick: () => data.lines.push({ name: "", quantity: 1, vatRate: 0, unitPrice: 0 }) },
                    [m("i.icon.plus"), "Add line"]
                ))))
            ),
        ]);
    }
};

const edit = {
    view: node => m(editable, {
        id: node.attrs.id,
        company: node.attrs.company,
        onSave: () => invoice.current.update(),
        onInit: () => invoice.current.load(node.attrs.id),
        onCancelHref: "/companies/" + node.attrs.company + "/invoices/" + node.attrs.id,
        title: "New Invoice"
    })
};

const create = {
    view: node => m(editable, {
        company: node.attrs.company,
        onSave: () => invoice.current.create(),
        onInit: () => invoice.current.init(),
        onCancelHref: "/companies/" + node.attrs.company + "/invoices"
    })
};

module.exports = {
    list: list,
    view: view,
    edit: edit,
    create: create
};