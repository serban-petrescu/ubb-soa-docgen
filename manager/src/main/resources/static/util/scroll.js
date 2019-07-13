
function debounce(func, wait) {
    let timeout;
    return function (...args) {
        clearTimeout(timeout);
        timeout = setTimeout(() => {
            func.apply(this, args);
        }, wait);
    };
}

module.exports = function (callback) {
    const debounced = debounce(callback, 100);
    return function() {
        if ((window.innerHeight + window.scrollY) >= document.body.scrollHeight - 100) {
            debounced();
        }
    }
}