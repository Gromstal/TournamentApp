document.addEventListener("DOMContentLoaded", function () {
    const tourInput = document.getElementById("tourCount");
    if (!tourInput) return;

    tourInput.addEventListener("input", function () {
        const value = Number(this.value);
        if (value > 5) {
            alert("Максимальное количество туров — 5");
            this.value = 5;
        }
    });
});