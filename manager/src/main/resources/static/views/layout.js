const menu = require("../components/menu");
const footer = require("../components/footer");

module.exports = {
    view: node => m("div", [
        m(menu, { route: node.attrs.route }),
        m("div.ui.container.segment.main-segment", node.children),
        m(footer)
    ])
};