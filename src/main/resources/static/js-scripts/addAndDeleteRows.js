function addRow() {
    const tableBody = document.getElementById('playerTableBody');
    const template = document.getElementById('rowTemplate');
    const newRow = document.importNode(template.content, true);
    tableBody.appendChild(newRow);
    reindexRows();
}

function removeRow(row) {
    row.remove();
    reindexRows();
}

function reindexRows() {
    const rows = document.querySelectorAll('#playerTableBody .player-row');
    rows.forEach((row, index) => {
        const inputs = row.querySelectorAll('input');
        if (inputs.length === 2) {
            inputs[0].setAttribute('name', `playerList[${index}].name`);
            inputs[1].setAttribute('name', `playerList[${index}].faction`);
        }
    });
}

document.getElementById('playerForm').addEventListener('submit', function (event) {
    const nameInputs = document.querySelectorAll('#playerTableBody input[name$=".name"]');
    const names = [];
    let hasEmptyName = false;
    let hasDuplicates = false;

    nameInputs.forEach(input => {
        const trimmedName = input.value.trim();
        if (trimmedName === '') {
            hasEmptyName = true;
        } else {
            if (names.includes(trimmedName)) {
                hasDuplicates = true;
            } else {
                names.push(trimmedName);
            }
        }
    });

    if (hasEmptyName) {
        alert('У всех игроков должно быть указано имя!');
        event.preventDefault();
        return;
    }

    if (hasDuplicates) {
        alert('Имена игроков должны быть уникальными!');
        event.preventDefault();
    }
});