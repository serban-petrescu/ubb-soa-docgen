// global m
const layout = require("./views/layout");
const persons = require("./views/persons");
const companies = require("./views/companies");
const invoices = require("./views/invoices");

m.route(document.body, "/", {
    "/": {
        render: () => m(layout, { route: "/" },
            m(companies.list, { title: "My Companies", target: "/companies/:id/invoices", onlyManaged: true }))
    },
    "/persons": {
        render: () => m(layout, { route: "/persons" }, m(persons.list))
    },
    "/persons/new": {
        render: () => m(layout, { route: "/persons" }, m(persons.create))
    },
    "/persons/:id": {
        render: node => m(layout, { route: "/persons" }, m(persons.view, node.attrs))
    },
    "/persons/:id/edit": {
        render: node => m(layout, { route: "/persons" }, m(persons.edit, node.attrs))
    },
    "/companies": {
        render: () => m(layout, { route: "/companies" }, m(companies.list, { title: "Companies", target: "/companies/:id" }))
    },
    "/companies/new": {
        render: () => m(layout, { route: "/companies" }, m(companies.create))
    },
    "/companies/new-managed": {
        render: () => m(layout, { route: "/companies" }, m(companies.create, { managed: true }))
    },
    "/companies/:id": {
        render: node => m(layout, { route: "/companies" }, m(companies.view, node.attrs))
    },
    "/companies/:id/edit": {
        render: node => m(layout, { route: "/companies" }, m(companies.edit, node.attrs))
    },
    "/companies/:company/invoices": {
        render: node => m(layout, m(invoices.list, node.attrs))
    },
    "/companies/:company/invoices/new": {
        render: node => m(layout, m(invoices.create, node.attrs))
    },
    "/companies/:company/invoices/:id": {
        render: node => m(layout, m(invoices.view, node.attrs))
    },
    "/companies/:company/invoices/:id/edit": {
        render: node => m(layout, m(invoices.edit, node.attrs))
    }
});