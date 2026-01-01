(function () {
    const input = document.getElementById('excelFile');
    const status = document.getElementById('excelStatus');
    const tbody = document.getElementById('playerTableBody');

    if (!input || !status || !tbody) return;

    function setOk(msg) { status.textContent = msg; status.style.color = 'green'; }
    function setErr(msg) { status.textContent = msg; status.style.color = 'crimson'; }

    function getRowInputs(row) {
        const inputs = row.querySelectorAll('input');
        return { name: inputs[0], faction: inputs[1] };
    }

    function ensureRows(count) {
        while (tbody.querySelectorAll('tr.player-row').length < count) addRow();
    }

    function normalizeCell(v) {
        return (v == null ? '' : String(v)).trim();
    }

    input.addEventListener('change', async () => {
        status.textContent = '';

        const file = input.files && input.files[0];
        if (!file) return;

        const ext = (file.name.split('.').pop() || '').toLowerCase();
        if (ext !== 'xlsx') {
            input.value = '';
            setErr('Загрузи .xlsx (для импорта в браузере)');
            return;
        }

        try {
            const buffer = await file.arrayBuffer();
            const wb = XLSX.read(buffer, { type: 'array' });

            const sheetName = wb.SheetNames[0];
            const ws = wb.Sheets[sheetName];


            const rows = XLSX.utils.sheet_to_json(ws, { header: 1, blankrows: false });
            const data = rows
                .map(r => [normalizeCell(r[0]), normalizeCell(r[1])])
                .filter(([name, faction]) => name.length > 0 || faction.length > 0);

            if (data.length && data[0][0].toLowerCase() === 'имя') data.shift();

            if (data.length === 0) {
                setErr('Файл пуст');
                return;
            }

            ensureRows(data.length);

            const trs = Array.from(tbody.querySelectorAll('tr.player-row'));
            for (let i = 0; i < data.length; i++) {
                const { name, faction } = getRowInputs(trs[i]);
                name.value = data[i][0] || '';
                faction.value = data[i][1] || '';
            }

            setOk('Файл импортирован');
        } catch (e) {
            console.error(e);
            setErr('Не удалось прочитать файл');
        }
    });
})();
