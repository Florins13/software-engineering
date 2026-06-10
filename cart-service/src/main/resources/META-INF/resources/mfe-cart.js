console.log("Loading script of mfe2!")


mfe('mfe2', ({ root, triggerMfeEvent, listenMfeEvent, reloadMfe, mfeEvents }) => {
    const checkoutButton = root?.querySelector('#triggerOrder');
    const increaseQuantity = root?.querySelectorAll('#increaseQuantity');
    const decreaseQuantity = root?.querySelectorAll('#decreaseQuantity');
    const deleteCartItem = root?.querySelectorAll('#deleteCartItem');

    if (checkoutButton) {
        checkoutButton.addEventListener('click', () => {
            finalizeOrder().then(() => triggerMfeEvent('trigger order event', {type: mfeEvents.RELOAD}));
        });
    }
    if (increaseQuantity && decreaseQuantity && deleteCartItem) {
        increaseQuantity.forEach(button => button.addEventListener('click', () => {
            handleQuantity('increase', button.getAttribute("value")).then(() => reloadMfe());
        }));
        decreaseQuantity.forEach(button => button.addEventListener('click', () => {
            handleQuantity('decrease', button.getAttribute("value")).then(() => reloadMfe());
        }));
        deleteCartItem.forEach(button => button.addEventListener('click', () => {
            deleteItem(button.getAttribute("value")).then(() => reloadMfe());
        }));
    }

    listenMfeEvent((e) => {
        if(e.detail?.payload?.type === 'add'){
            addBikeToCart(e.detail?.payload?.id).then(() => reloadMfe());
        }
    });

});

async function makeApiCall({ endpoint, method = 'GET' }) {
    const url = `http://localhost:8082${endpoint}`;
    try {
        const response = await fetch(url, { method, headers: {} });
        if (!response.ok) {
            console.error('API call failed with status:', response.status);
        }
        return response;
    } catch (error) {
        console.error(`Error during ${method} to ${url}:`, error);
        throw error;
    }
}

async function finalizeOrder() {
    await makeApiCall({ endpoint: '/order/finalise', method: 'POST' });
}

async function handleQuantity(type, id) {
    await makeApiCall({ endpoint: `/cart/updateQuantity/${id}/${type}`, method: 'POST' });
}

async function deleteItem(id) {
    await makeApiCall({ endpoint: `/cart/delete/${id}`, method: 'POST' });
}

async function addBikeToCart(id) {
    await makeApiCall({ endpoint: `/cart/add/${id}`, method: 'POST' });
}