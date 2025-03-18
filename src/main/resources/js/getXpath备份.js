(function () {
    const handleClick = function (e) {
        if (e.button === 2) {
            e.preventDefault();
            let el = e.target;
            let path = [];

            function isUniqueSelector(selector) {
                return document.querySelectorAll(selector).length === 1;
            }

            while (el && el.nodeType === 1) {
                let seg = null;

                // 尝试使用唯一的 id
                if (el.id && isUniqueSelector(`#${el.id}`)) {
                    seg = `${el.tagName.toLowerCase()}[@id="${el.id}"]`;
                    path.unshift(seg);
                    break;
                }

                // 尝试使用唯一的 class
                if (el.classList.length > 0) {
                    for (let className of el.classList) {
                        if (isUniqueSelector(`.${className}`)) {
                            seg = `${el.tagName.toLowerCase()}[contains(@class, "${className}")]`;
                            path.unshift(seg);
                            break;
                        }
                    }
                    if (seg) break;
                }

                // 尝试使用唯一的 attribute
                const attributes = el.attributes;
                for (let i = 0; i < attributes.length; i++) {
                    const attr = attributes[i];
                    if (attr.name !== 'id' && attr.name !== 'class') {
                        const selector = `${el.tagName.toLowerCase()}[@${attr.name}="${attr.value}"]`;
                        if (isUniqueSelector(selector)) {
                            seg = selector;
                            path.unshift(seg);
                            break;
                        }
                    }
                }
                if (seg) break;

                // 使用父子层级
                seg = el.tagName.toLowerCase();
                let siblings = el.parentNode.children;
                let index = Array.from(siblings).indexOf(el) + 1;
                if (index > 1) seg += `[${index}]`;
                path.unshift(seg);
                el = el.parentNode;
            }

            javaBridge.onXPath('//' + path.join('/'));
        }
    };
    const handleMouseOver = function (e) {
        e.target.style.outline = '1px solid black';
    };
    const handleMouseOut = function (e) {
        e.target.style.outline = '';
    };
    document.addEventListener('contextmenu', handleClick, true);
    document.addEventListener('mouseover', handleMouseOver, true);
    document.addEventListener('mouseout', handleMouseOut, true);
    new MutationObserver(function (mutations) {
        mutations.forEach(function (m) {
            Array.from(m.addedNodes).forEach(function (n) {
                if (n.nodeType === 1) {
                    n.addEventListener('contextmenu', handleClick);
                    n.addEventListener('mouseover', handleMouseOver);
                    n.addEventListener('mouseout', handleMouseOut);
                }
            });
        });
    }).observe(document.documentElement, {
        childList: true, subtree: true
    });
})();