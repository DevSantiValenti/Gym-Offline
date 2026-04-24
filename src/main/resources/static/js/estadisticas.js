// document.addEventListener("DOMContentLoaded", function () {

//     const ctx = document.getElementById("barChart");
//     if (!ctx || !window.dataValues || !window.labels) return;

//     const valores = window.dataValues;
//     const etiquetas = window.labels;

//     /* =========================
//        COLORES POR MES
//        ========================= */
//     const coloresMeses = [
//         '#9BD0F5', // Enero
//         '#FFB1C1', // Febrero
//         '#FFD966', // Marzo
//         '#A5D6A7', // Abril
//         '#CE93D8', // Mayo
//         '#FFCC80', // Junio
//         '#90CAF9', // Julio
//         '#F48FB1', // Agosto
//         '#B39DDB', // Septiembre
//         '#80CBC4', // Octubre
//         '#E6EE9C', // Noviembre
//         '#BCAAA4'  // Diciembre
//     ];

//     /* =========================
//        MEJOR MES (MAXIMO)
//        ========================= */
//     const maxValor = Math.max(...valores);
//     const indexMax = valores.indexOf(maxValor);

//     // Copias para no modificar los originales
//     const backgroundColors = [...coloresMeses];
//     const borderColors = [...coloresMeses];

//     // 🎯 Pintar mejor mes en dorado
//     if (indexMax >= 0) {
//         backgroundColors[indexMax] = '#FFD700'; // dorado
//         borderColors[indexMax] = '#DAA520';
//     }

//     /* =========================
//        GRAFICO
//        ========================= */
//     new Chart(ctx, {
//         type: 'bar',
//         data: {
//             labels: etiquetas,
//             datasets: [{
//                 label: 'Total mensual ($)',
//                 data: valores,
//                 backgroundColor: backgroundColors,
//                 borderColor: borderColors,
//                 borderWidth: 1
//             }]
//         },
//         options: {
//             responsive: true,
//             plugins: {
//                 tooltip: {
//                     callbacks: {
//                         label: function (context) {
//                             return '$ ' + context.parsed.y.toLocaleString("es-AR");
//                         }
//                     }
//                 },
//                 title: {
//                     display: true,
//                     text: 'Ingresos mensuales'
//                 }
//             },
//             scales: {
//                 y: {
//                     beginAtZero: true,
//                     ticks: {
//                         callback: function (value) {
//                             return '$ ' + value.toLocaleString("es-AR");
//                         }
//                     }
//                 }
//             }
//         }
//     });

//     /* =========================
//        TEXTO: MEJOR MES
//        ========================= */
//     const mejorMesEl = document.getElementById("mejorMes");
//     if (mejorMesEl && indexMax >= 0) {
//         mejorMesEl.innerText =
//             etiquetas[indexMax] + " – $ " + maxValor.toLocaleString("es-AR");
//     }

//     /* =========================
//        TEXTO: TOTAL ANUAL
//        ========================= */
//     const total = valores.reduce((acc, val) => acc + val, 0);
//     const totalAnualEl = document.getElementById("totalAnual");
//     if (totalAnualEl) {
//         totalAnualEl.innerText =
//             "$ " + total.toLocaleString("es-AR");
//     }

//     // --- Chart: Inscripciones mensuales (nuevo) ---
//     const inscripciones = window.inscripciones;
//     if (document.getElementById("inscripcionesChart") && Array.isArray(inscripciones) && inscripciones.length) {
//         new Chart(document.getElementById("inscripcionesChart"), {
//             type: 'bar',
//             data: {
//                 labels: etiquetas,
//                 datasets: [{
//                     label: 'Inscripciones (cantidad)',
//                     data: inscripciones,
//                     backgroundColor: coloresMeses.map(c => c.replace('#', '80') ? c : c).map((c, i) => {
//                         // color ligeramente distinto (tono sólido)
//                         return '#4caf50';
//                     }),
//                     borderColor: '#388e3c',
//                     borderWidth: 1
//                 }]
//             },
//             options: {
//                 responsive: true,
//                 plugins: {
//                     tooltip: {
//                         callbacks: {
//                             label: function (context) {
//                                 return '' + context.parsed.y.toLocaleString("es-AR");
//                             }
//                         }
//                     },
//                     title: {
//                         display: true,
//                         text: 'Inscripciones por mes'
//                     }
//                 },
//                 scales: {
//                     y: {
//                         beginAtZero: true,
//                         ticks: {
//                             callback: function (value) {
//                                 return value.toLocaleString("es-AR");
//                             }
//                         }
//                     }
//                 }
//             }
//         });
//     }
// });
document.addEventListener("DOMContentLoaded", function () {

    const ctx = document.getElementById("barChart");
    if (!ctx || !window.dataValues || !window.labels) return;

    const valores = window.dataValues;
    const etiquetas = window.labels;

    /* =========================
       COLORES POR MES
       ========================= */
    const coloresMeses = [
        '#9BD0F5', '#FFB1C1', '#FFD966', '#A5D6A7',
        '#CE93D8', '#FFCC80', '#90CAF9', '#F48FB1',
        '#B39DDB', '#80CBC4', '#E6EE9C', '#BCAAA4'
    ];

    /* =========================
       MEJOR MES
       ========================= */
    const maxValor = Math.max(...valores);
    const indexMax = valores.indexOf(maxValor);

    const backgroundColors = [...coloresMeses];
    const borderColors = [...coloresMeses];

    if (indexMax >= 0) {
        backgroundColors[indexMax] = '#FFD700'; // dorado
        borderColors[indexMax] = '#DAA520';
    }

    /* =========================
       CHART INGRESOS
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
                borderWidth: 1,
                borderRadius: 6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false, // 🔥 CLAVE PARA MÓVIL
            plugins: {
                legend: { display: false },
                tooltip: {
                    callbacks: {
                        label: ctx => `$ ${ctx.parsed.y.toLocaleString("es-AR")}`
                    }
                },
                title: {
                    display: true,
                    text: 'Ingresos mensuales'
                }
            },
            scales: {
                x: {
                    ticks: {
                        maxRotation: 45,
                        minRotation: 45
                    }
                },
                y: {
                    beginAtZero: true,
                    ticks: {
                        callback: v => `$ ${v.toLocaleString("es-AR")}`
                    }
                }
            }
        }
    });

    /* =========================
       TEXTO RESUMEN
       ========================= */
    const mejorMesEl = document.getElementById("mejorMes");
    if (mejorMesEl && indexMax >= 0) {
        mejorMesEl.textContent =
            `${etiquetas[indexMax]} – $ ${maxValor.toLocaleString("es-AR")}`;
    }

    const totalAnualEl = document.getElementById("totalAnual");
    if (totalAnualEl) {
        const total = valores.reduce((a, b) => a + b, 0);
        totalAnualEl.textContent = `$ ${total.toLocaleString("es-AR")}`;
    }

    /* =========================
       CHART PAGOS DE CUOTA
       ========================= */
    if (window.cuotas && document.getElementById("cuotasChart")) {
        new Chart(document.getElementById("cuotasChart"), {
            type: 'bar',
            data: {
                labels: etiquetas,
                datasets: [{
                    label: 'Pago de cuota',
                    data: window.cuotas,
                    backgroundColor: '#4e79ff',
                    borderColor: '#2f56d6',
                    borderWidth: 1,
                    borderRadius: 6
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false },
                    title: {
                        display: true,
                        text: 'Pagos de cuota por mes'
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            precision: 0
                        }
                    }
                }
            }
        });
    }

    /* =========================
       CHART INSCRIPCIONES
       ========================= */
    if (window.inscripciones && document.getElementById("inscripcionesChart")) {
        new Chart(document.getElementById("inscripcionesChart"), {
            type: 'line',
            data: {
                labels: etiquetas,
                datasets: [{
                    label: 'Inscripciones',
                    data: window.inscripciones,
                    borderColor: '#00ff9c',
                    backgroundColor: 'rgba(0,255,156,.15)',
                    tension: 0.4,
                    fill: true,
                    pointRadius: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false },
                    title: {
                        display: true,
                        text: 'Inscripciones por mes'
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            precision: 0
                        }
                    }
                }
            }
        });
    }

    // Chart Gastos:
    if (window.gastos && document.getElementById("balanceChart")) {

        new Chart(document.getElementById("balanceChart"), {
            type: 'bar',
            data: {
                labels: etiquetas,
                datasets: [
                    {
                        label: 'Ingresos',
                        data: valores,
                        backgroundColor: '#00ff9c'
                    },
                    {
                        label: 'Gastos',
                        data: window.gastos,
                        backgroundColor: '#ff4d4d'
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    title: {
                        display: true,
                        text: 'Ingresos vs Gastos'
                    }
                }
            }
        });
    }

});
