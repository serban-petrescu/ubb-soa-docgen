// global m
const company = require("../models").company;
const scroll = require("../util/scroll");
const loader = require("../components/loader");
const address = require("../components/address");

const onScroll = scroll(company.page.load);

const list = {
    oninit(node) {
        window.addEventListener("scroll", onScroll);
        return company.page.init({"only-managed": node.attrs.onlyManaged || false});
    },
    onupdate(node) {
        if ((node.attrs.onlyManaged || false) !== company.page.filter["only-managed"]) {
            company.page.init({"only-managed": node.attrs.onlyManaged || false});
        }
    },
    onremove() {
        window.removeEventListener("scroll", onScroll);
    },
    view: node => m("div", [
        m("a.ui.button.positive.icon.float-right", { 
            href: "/companies/new" + (node.attrs.onlyManaged ? "-managed" : ""), oncreate: m.route.link 
        }, m("i.icon.plus")),
        m("h3.ui.header.header-with-button", node.attrs.title),
        m("div.ui.middle.aligned.selection.list.animated.divided.large", company.page.content
            .map(comp => m("div.item", m("div.content",
                m("a.header", { href: node.attrs.target.replace(":id", comp.id), oncreate: m.route.link }, comp.name)
            )))
        )
    ])
};

const view = {
    oninit: node => company.current.load(node.attrs.id),
    view() {
        if (company.current.loading) {
            return m(loader);
        }
        const data = company.current.data;
        return m("form.ui.form", [
            m("a.ui.button.primary.icon.float-right", { href: "/companies/" + data.id + "/edit", oncreate: m.route.link }, 
                m("i.icon.edit")),
            m("h3.ui.header.header-with-button", "Company: " + data.name),
            m("h4.ui.dividing.header", "General Details"),
            m("div.two.fields", [
                m("div.field", [m("label", "Name"), m("div.field", data.name)]),
                m("div.field", [m("label", "Email"), m("div.field", m("a", { href: "mailto:" + data.email },
                    data.email))]),
            ]),
            m("div.two.fields", [
                m("div.field", [m("label", "Registration Number"), m("div.field", data.identification.registration)]),
                m("div.field", [m("label", "Identification Number"), m("div.field", data.identification.number)]),
            ]),
            m("h4.ui.dividing.header", "Financial"),
            m("div.two.fields", [
                m("div.field", [m("label", "Bank"), m("div.field", data.bankAccount.bank)]),
                m("div.field", [m("label", "Account IBAN"), m("div.field", data.bankAccount.iban)]),
            ]),
            m("div.two.fields", [
                m("div.field", [m("label", "Pays VAT?"), m("div.field", data.vat ? "Yes" : "No")]),
                m("div.field", [m("label", "Capital"), m("div.field", data.capital ? (data.capital + " RON") : "-")]),
            ]),
            m("h4.ui.dividing.header", "Headquarters"),
            m(address.view, data.headquarters),
            m("h4.ui.dividing.header", "Office"),
            m(address.view, data.office)
        ]);
    }
};

const editable = {
    oninit: node => node.attrs.onInit(),
    view(node) {
        if (company.current.loading) {
            return m(loader);
        }
        const data = company.current.data;
        return m("form.ui.form", [
            m("a.ui.button.positive.icon.float-right", {
                onclick: () => node.attrs.onSave().then(data => m.route.set("/companies/" + data.id)),
            }, m("i.icon.save")),
            m("a.ui.button.negative.icon.float-right", { href: node.attrs.onCancelHref, oncreate: m.route.link },
                m("i.icon.cancel")),
            m("h3.ui.header.header-with-button", node.attrs.title || ("Company: " + data.name)),
            m("h4.ui.dividing.header", "General Details"),
            m("div.two.fields", [
                m("div.field", [m("label", "Name"), m("div.field", m("input", {
                    type: "text", placeholder: "Name", value: data.name, 
                    oninput: e => data.name = e.target.value
                }))]),
                m("div.field", [m("label", "Email"), m("div.field", m("input", {
                    type: "text", placeholder: "Email", value: data.email, 
                    oninput: e => data.email = e.target.value
                }))]),
            ]),
            m("div.two.fields", [
                m("div.field", [m("label", "Registration Number"), m("div.field", m("input", {
                    type: "text", placeholder: "Registration Number", value: data.identification.registration, 
                    oninput: e => data.identification.registration = e.target.value
                }))]),
                m("div.field", [m("label", "Identification Number"), m("div.field", m("input", {
                    type: "text", placeholder: "Identification Number", value: data.identification.number, 
                    oninput: e => data.identification.number = e.target.value
                }))]),
            ]),
            m("h4.ui.dividing.header", "Financial"),
            m("div.two.fields", [
                m("div.field", [m("label", "Bank"), m("div.field", m("input", {
                    type: "text", placeholder: "Bank", value: data.bankAccount.bank, 
                    oninput: e => data.bankAccount.bank = e.target.value
                }))]),
                m("div.field", [m("label", "Account IBAN"), m("div.field", m("input", {
                    type: "text", placeholder: "Account IBAN", value: data.bankAccount.iban, 
                    oninput: e => data.bankAccount.iban = e.target.value
                }))]),
            ]),
            m("div.two.fields", [
                m("div.field", [m("label", "Pays VAT?"), m("div.field", m("div.ui.checkbox",
                    { oncreate: node => $(node.dom).checkbox() }, 
                    m("input.hidden", {
                        checked: data.vat, type: "checkbox",
                        onchange: e => data.vat = e.target.checked
                    })
                ))]),
                m("div.field", [m("label", "Capital"), m("div.field", m("input", {
                    type: "number", placeholder: "Capital", step: "0.01", value: data.capital, 
                    oninput: e => data.capital = e.target.value
                }))]),
            ]),
            m("h4.ui.dividing.header", "Headquarters"),
            m(address.edit, { data: data.headquarters, onChange: (p, v) => data.headquarters[p] = v }),
            m("h4.ui.dividing.header", "Office"),
            m(address.edit, { data: data.office, onChange: (p, v) => data.office[p] = v })
        ]);
    }
};

const edit = {
    view: node => m(editable, {
        id: node.attrs.id,
        onSave: () => company.current.update(),
        onInit: () => company.current.load(node.attrs.id),
        onCancelHref: "/companies/" + node.attrs.id
    })
};

const create = {
    view: node => m(editable, {
        onSave: () => company.current.create(),
        onInit: () => company.current.init(node.attrs.managed || false),
        onCancelHref: "/companies",
        title: "New Company"
    })
};

module.exports = {
    list: list,
    view: view,
    edit: edit,
    create: create
};