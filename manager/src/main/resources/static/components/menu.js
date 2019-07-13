// global m

const isActive = (node, route) => node.attrs.route === route ? ".active" : "";

module.exports = {
    view: node => m("nav.ui.inverted.menu.attached", [
        m("div.ui.container", [
            m("div.item", [
                m("img.ui.image.mini", { src: "./assets/logo.png" })
            ]),
            m("a.item" + isActive(node, "/"), { href: "/", oncreate: m.route.link }, "Home"),
            m("a.item" + isActive(node, "/persons"), { href: "/persons", oncreate: m.route.link }, "People"),
            m("a.item" + isActive(node, "/companies"), { href: "/companies", oncreate: m.route.link }, "Companies"),
            m("div.grid.menu.right", m("a.item", { href: "/logout" }, m("i.icon.power")))
        ])
    ])
};