(function () {
    const handleContextMenu = function (e) {
        e.preventDefault();
        const elementType = e.target.tagName.toLowerCase();
        window.javaBridge.doOnContextMenu(elementType);
    }
    const capture = {
        capture: true
    }
    document.addEventListener('contextmenu', handleContextMenu, true);
    new MutationObserver(function (mutations) {
        mutations.forEach(function (m) {
            Array.from(m.addedNodes).forEach(function (n) {
                if (n.nodeType === 1) {
                    n.addEventListener('contextmenu', handleContextMenu, true);
                }
            });
        });
    }).observe(document.documentElement, {
        childList: true, subtree: true
    });
})();