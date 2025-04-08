(function () {
    // ==================== 1. 菜单容器初始化 ====================
    const menuId = 'javaCustomContextMenu';
    const menuHTML = `
        <div id="${menuId}" style="
            position: absolute;
            display: none;
            background: white;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-shadow: 2px 2px 10px rgba(0,0,0,0.2);
            z-index: 99999;
            padding: 4px 0;
            min-width: 160px;
            font-family: Arial, sans-serif;
        "></div>
    `;

    // 防止重复注入
    if (!document.getElementById(menuId)) {
        document.body.insertAdjacentHTML('beforeend', menuHTML);
    }
    const menu = document.getElementById(menuId);

    // ==================== 2. 构建菜单项 ====================
    function buildMenuItems() {
        try {
            // 清空现有菜单
            menu.innerHTML = '';

            // 检查Java是否传递了菜单数据
            if (!window.javaMenuItems || !Array.isArray(window.javaMenuItems)) {
                alert("[Java] 未接收到有效的菜单项数据");
                return;
            }

            // 动态生成菜单项
            window.javaMenuItems.forEach(item => {
                if (!item.id || !item.title) {
                    alert(`[Java] 无效的菜单项数据: ${JSON.stringify(item)}`);
                    return;
                }

                const menuItem = document.createElement('div');
                menuItem.className = 'java-menu-item';
                menuItem.dataset.id = item.id;
                menuItem.textContent = item.title;

                // 基础样式
                Object.assign(menuItem.style, {
                    padding: '6px 12px',
                    cursor: 'pointer',
                    whiteSpace: 'nowrap',
                    transition: 'background-color 0.2s'
                });

                // 交互效果
                menuItem.addEventListener('mouseenter', () => {
                    menuItem.style.backgroundColor = '#f5f5f5';
                });
                menuItem.addEventListener('mouseleave', () => {
                    menuItem.style.backgroundColor = '';
                });

                // 点击事件
                menuItem.addEventListener('click', handleMenuItemClick);

                menu.appendChild(menuItem);
            });
        } catch (e) {
            alert("[JS] 构建菜单失败: " + e.message);
        }
    }

    // 获取元素的XPath
    function getXpath(element) {
        let el = element;
        let path = [];

        function isUniqueSelector(xpath) {
            const result = document.evaluate(xpath, document.body, null, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);
            return result.snapshotLength === 1;
        }

        while (el && el.nodeType === 1) {
            let seg = null;

            // 尝试使用唯一的 id
            if (el.id && isUniqueSelector(`//${el.tagName.toLowerCase()}[@id="${el.id}"]`)) {
                seg = `${el.tagName.toLowerCase()}[@id="${el.id}"]`;
                path.unshift(seg);
                break;
            }

            // 尝试使用唯一的 class
            if (el.classList.length > 0) {
                for (let className of el.classList) {
                    if (isUniqueSelector(`//${el.tagName.toLowerCase()}[contains(@class, "${className}")]`)) {
                        seg = `${el.tagName.toLowerCase()}[contains(@class, "${className}")]`;
                        path.unshift(seg);
                        break;
                    }
                }
                if (seg) break;
            }

            // 尝试使用唯一的 attribute，暂时不使用属性选择器，因为属性值可能包含内容，存在产生错误xpath的可能性
            // const attributes = el.attributes;
            // for (let i = 0; i < attributes.length; i++) {
            //     const attr = attributes[i];
            //     if (attr.name !== 'id' && attr.name !== 'class' && attr.name !== 'style') {
            //         const xpath = `//${el.tagName.toLowerCase()}[@${attr.name}="${attr.value}"]`;
            //         if (isUniqueSelector(xpath)) {
            //             seg = `${el.tagName.toLowerCase()}[@${attr.name}="${attr.value}"]`;
            //             path.unshift(seg);
            //             break;
            //         }
            //     }
            // }
            if (seg) break;

            // 使用父子层级
            seg = el.tagName.toLowerCase();
            let siblings = el.parentNode.children;
            let index = Array.from(siblings).indexOf(el) + 1;
            if (index > 1) seg += `[${index}]`;
            path.unshift(seg);
            el = el.parentNode;
        }

        return '//' + path.join('/');
    }

    // ==================== 3. 菜单项点击处理 ====================
    function handleMenuItemClick(e) {
        try {
            e.stopPropagation();
            const menuItemId = this.dataset.id;

            // 获取右键点击时的元素信息
            const element = document.elementFromPoint(
                parseInt(menu.style.left),
                parseInt(menu.style.top)
            );

            if (!element) {
                alert("[JS] 未能定位到目标元素");
                return;
            }

            const xpath = getXpath(element);

            // 回调Java并传递菜单ID和元素信息
            if (window.javaBridge && typeof window.javaBridge.onMenuItemClicked === 'function') {
                window.javaBridge.onMenuItemClicked(
                    menuItemId,
                    xpath
                );
            } else {
                alert("[JS] Java回调接口未就绪");
            }

            hideMenu();
        } catch (error) {
            alert("[JS] 菜单点击处理失败: " + error.message);
        }
    }

    // ==================== 4. 右键菜单控制 ====================
    function showMenu(x, y) {
        Object.assign(menu.style, {
            display: 'block',
            left: `${x}px`,
            top: `${y}px`
        });
        document.addEventListener('click', hideMenuOnClickOutside);
    }

    function hideMenu() {
        menu.style.display = 'none';
        document.removeEventListener('click', hideMenuOnClickOutside);
    }

    function hideMenuOnClickOutside(e) {
        if (!menu.contains(e.target)) {
            hideMenu();
        }
    }

    // ==================== 5. 事件监听 ====================
    document.addEventListener('contextmenu', (e) => {
        try {
            e.preventDefault();
            showMenu(e.clientX, e.clientY);
        } catch (error) {
            alert("[JS] 右键事件处理失败: " + error.message);
        }
    }, true);

    // 监听DOM变化确保新元素也能触发右键
    new MutationObserver(() => {
        // 可在此处添加对新元素的右键监听
    }).observe(document.documentElement, {
        childList: true,
        subtree: true
    });

    // ==================== 6. 初始化 ====================
    // 当Java完成绑定后调用此方法
    window.initJavaContextMenu = function () {
        buildMenuItems();
    };

    // 如果Java已经完成绑定，立即初始化
    if (window.javaBridge && window.javaMenuItems) {
        buildMenuItems();
    }
})();