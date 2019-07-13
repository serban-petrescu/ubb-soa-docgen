// global m

const view = {
    view: node => m("div", [
        m("div.two.fields", [
            m("div.field", [m("label", "Country"), m("div.field", node.attrs.country)]),
            m("div.field", [m("label", "Region"), m("div.field", node.attrs.region)])
        ]),
        m("div.two.fields", [
            m("div.field", [m("label", "Street Address"), m("div.field", node.attrs.streetAddress)]),
            m("div.field", [m("label", "Postal Code"), m("div.field", node.attrs.postalCode)])
        ])
    ])
};


const edit = {
    view: node => m("div", [
        m("div.two.fields", [
            m("div.field", [m("label", "Country"), m("div.field", m("input", {
                type: "text", placeholder: "Country",
                value: node.attrs.data.country, oninput: e => node.attrs.onChange("country", e.target.value)
            }))]),
            m("div.field", [m("label", "Region"), m("div.field", m("input", {
                type: "text", placeholder: "Region",
                value: node.attrs.data.region, oninput: e => node.attrs.onChange("region", e.target.value)
            }))])
        ]),
        m("div.two.fields", [
            m("div.field", [m("label", "Street Address"), m("div.field", m("input", {
                type: "text", placeholder: "Street Address",
                value: node.attrs.data.streetAddress, oninput: e => node.attrs.onChange("streetAddress", e.target.value)
            }))]),
            m("div.field", [m("label", "Postal Code"), m("div.field", m("input", {
                type: "text", placeholder: "Postal Code",
                value: node.attrs.data.postalCode, oninput: e => node.attrs.onChange("postalCode", e.target.value)
            }))])
        ])
    ])
}

module.exports = {
    view: view,
    edit: edit
}