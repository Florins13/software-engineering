console.log("Loading script of mfe1!")


mfe('mfe1', ({root, triggerMfeEvent}) => {
    const btns = root?.querySelectorAll('button')
    btns?.forEach(button => {
        button.addEventListener('click', () => {
            triggerMfeEvent(`add to cart bike with ${button.getAttribute("mfe-id")}`, {type: 'add', id: button.getAttribute("mfe-id")})
        });
    })
})

// different way, mfe on element instead of window
// const mfeElement = document.querySelector('[mfe-name="mfe1"]');
// console.log(mfeElement);
// mfeElement.mfe('mfe1', ({root, triggerMfeEvent}) => {
//     const btns = root?.querySelectorAll('button')
//     btns?.forEach(button => {
//         button.addEventListener('click', () => {
//             triggerMfeEvent(`add to cart bike with ${button.getAttribute("mfe-id")}`, {type: 'add', id: button.getAttribute("mfe-id")})
//         });
//     })
// })