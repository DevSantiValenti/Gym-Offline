document.addEventListener("DOMContentLoaded", function () {

    const ctx = document.getElementById("barChart");
    if (!ctx || !window.dataValues || !window.labels) return;

    const valores = window.dataValues;
    const etiquetas = window.labels;

    /* =========================
       COLORES POR MES
       ========================= */
    const coloresMeses = [
        '#9BD0F5', // Enero
        '#FFB1C1', // Febrero
        '#FFD966', // Marzo
        '#A5D6A7', // Abril
        '#CE93D8', // Mayo
        '#FFCC80', // Junio
        '#90CAF9', // Julio
        '#F48FB1', // Agosto
        '#B39DDB', // Septiembre
        '#80CBC4', // Octubre
        '#E6EE9C', // Noviembre
        '#BCAAA4'  // Diciembre
    ];

    /* =========================
       MEJOR MES (MAXIMO)
       ========================= */
    const maxValor = Math.max(...valores);
    const indexMax = valores.indexOf(maxValor);

    // Copias para no modificar los originales
    const backgroundColors = [...coloresMeses];
    const borderColors = [...coloresMeses];

    // 🎯 Pintar mejor mes en dorado
    if (indexMax >= 0) {
        backgroundColors[indexMax] = '#FFD700'; // dorado
        borderColors[indexMax] = '#DAA520';
    }

    /* =========================
       GRAFICO
       ========================= */
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: etiquetas,
            datasets: [{
                label: 'Total mensual ($)',
                data: valores,
                backgroundColor: backgroundColors,
                borderColor: borderColors,
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            plugins: {
                tooltip: {
                    callbacks: {
                        label: function (context) {
                            return '$ ' + context.parsed.y.toLocaleString("es-AR");
                        }
                    }
                },
                title: {
                    display: true,
                    text: 'Ingresos mensuales'
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: function (value) {
                            return '$ ' + value.toLocaleString("es-AR");
                        }
                    }
                }
            }
        }
    });

    /* =========================
       TEXTO: MEJOR MES
       ========================= */
    const mejorMesEl = document.getElementById("mejorMes");
    if (mejorMesEl && indexMax >= 0) {
        mejorMesEl.innerText =
            etiquetas[indexMax] + " – $ " + maxValor.toLocaleString("es-AR");
    }

    /* =========================
       TEXTO: TOTAL ANUAL
       ========================= */
    const total = valores.reduce((acc, val) => acc + val, 0);
    const totalAnualEl = document.getElementById("totalAnual");
    if (totalAnualEl) {
        totalAnualEl.innerText =
            "$ " + total.toLocaleString("es-AR");
    }

    // --- Chart: Inscripciones mensuales (nuevo) ---
    const inscripciones = window.inscripciones;
    if (document.getElementById("inscripcionesChart") && Array.isArray(inscripciones) && inscripciones.length) {
        new Chart(document.getElementById("inscripcionesChart"), {
            type: 'bar',
            data: {
                labels: etiquetas,
                datasets: [{
                    label: 'Inscripciones (cantidad)',
                    data: inscripciones,
                    backgroundColor: coloresMeses.map(c => c.replace('#', '80') ? c : c).map((c, i) => {
                        // color ligeramente distinto (tono sólido)
                        return '#4caf50';
                    }),
                    borderColor: '#388e3c',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    tooltip: {
                        callbacks: {
                            label: function (context) {
                                return '' + context.parsed.y.toLocaleString("es-AR");
                            }
                        }
                    },
                    title: {
                        display: true,
                        text: 'Inscripciones por mes'
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function (value) {
                                return value.toLocaleString("es-AR");
                            }
                        }
                    }
                }
            }
        });
    }
});
