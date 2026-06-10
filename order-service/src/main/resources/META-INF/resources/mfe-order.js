console.log("Loading script of mfe3!")

const mfeTriggerBikeEvent = "triggerBikeEvent";
const mfeTriggerCartEvent = "triggerCartEvent";

mfe("mfe3", ({root, triggerMfeEvent, listenMfeEvent, mfeEvents, reloadMfe}) => {
    const form = root.querySelector("form[action='/order/finalise']");

    if (form) {
        form.addEventListener("submit", async (event) => {
            event.preventDefault();

            const formData = new FormData(form);
            const payload = {
                fullName:    formData.get("fullName"),
                address:     formData.get("address"),
                telephone:   parseInt(formData.get("telephone"), 10),
                zipCode:     formData.get("zipCode"),
                acquireType: formData.get("acquireType") ?? "buy"
            };

            try {
                const response = await fetch("http://localhost:8083/order/finalise", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(payload)
                });

                if (response.ok) {
                    console.log("Order finalised successfully");
                } else {
                    const err = await response.json().catch(() => ({}));
                    console.error("Finalise order failed:", response.status, err);
                }
            } catch (error) {
                console.error("Error during POST to /order/finalise:", error);
            }
        });
    }

    listenMfeEvent(() => {
        triggerMfeEvent("trigger bike event", { type: mfeEvents.RELOAD }, mfeTriggerBikeEvent);
        triggerMfeEvent("trigger cart event", { type: mfeEvents.RELOAD }, mfeTriggerCartEvent);
    });
})