(function () {
    const tbody = document.getElementById('playerTableBody');

    function getRowInputs(row) {
        const inputs = row.querySelectorAll('input');
        return { name: inputs[0], faction: inputs[1] };
    }

    function ensureRows(count) {
        while (tbody.querySelectorAll('tr.player-row').length < count) {
            addRow();
        }
    }

    function parseClipboard(text) {
        const lines = text.replace(/\r/g, '').split('\n');
        return lines
            .map(line => line.replace(/\s+$/g, ''))
            .filter(line => line.trim().length > 0)
            .map(line => line.split('\t').map(s => s.trim()));
    }

    tbody.addEventListener('paste', (e) => {
        const target = e.target;
        if (!(target instanceof HTMLInputElement)) return;

        const row = target.closest('tr.player-row');
        if (!row) return;

        const { name: nameInput, faction: factionInput } = getRowInputs(row);

        const isNameCol = target === nameInput;
        const isFactionCol = target === factionInput;
        if (!isNameCol && !isFactionCol) return;

        const text = (e.clipboardData || window.clipboardData).getData('text');
        if (!text) return;

        const grid = parseClipboard(text);
        const looksMultiRow = grid.length > 1;
        const looksMultiCol = grid.some(r => r.length > 1);

        if (!looksMultiRow && !looksMultiCol) return;

        e.preventDefault();

        const rows = Array.from(tbody.querySelectorAll('tr.player-row'));
        const startIndex = rows.indexOf(row);

        ensureRows(startIndex + grid.length);

        const updatedRows = Array.from(tbody.querySelectorAll('tr.player-row'));

        for (let i = 0; i < grid.length; i++) {
            const currentRow = updatedRows[startIndex + i];
            const { name, faction } = getRowInputs(currentRow);

            const cols = grid[i];
            const col0 = (cols[0] || '').trim();
            const col1 = (cols[1] || '').trim();

            if (isNameCol) {
                if (col0) name.value = col0;
                if (looksMultiCol && faction && col1) faction.value = col1;
            } else if (isFactionCol) {
                if (faction && col0) faction.value = col0;
            }
        }
    });
})();