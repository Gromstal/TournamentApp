document.addEventListener("DOMContentLoaded", function () {


    document.querySelectorAll("input[type='number']").forEach(function (input) {
        input.addEventListener("focus", function () {
            this.value = "";
        });

        input.addEventListener("input", function () {
            if (this.value.length > 10) {
                this.value = this.value.slice(0, 10);
            }
        });
    });


    document.querySelectorAll("form").forEach(function (form) {
        form.addEventListener("submit", function (event) {
            let emptyFound = false;

            this.querySelectorAll("input[type='number']").forEach(function (input) {
                if (input.value.trim() === "") {
                    emptyFound = true;
                    input.classList.add("error");
                } else {
                    input.classList.remove("error");
                }
            });

            if (emptyFound) {
                event.preventDefault();
                alert("Пожалуйста, заполните все поля!");
            }
        });
    });

});