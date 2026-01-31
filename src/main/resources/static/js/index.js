//Limpiar socio
let timerLimpieza = null;

// 🎵 Sonidos
const sonidoOk = new Audio('/sounds/ok.mp3');
const sonidoVencida = new Audio('/sounds/vencida.mp3');

document.getElementById('busqueda').addEventListener('keypress', function (e) {
    if (e.key === 'Enter') {
        const dni = this.value.trim();
        // console.log("Buscando DNI:", dni);
        if (dni === '') return;

        fetch(`/api/socios/dni?dni=${encodeURIComponent(dni)}`)
            .then(response => {
                // console.log("Respuesta cruda:", response);
                if (!response.ok) throw new Error('Socio no encontrado');
                return response.json();
            })
            .then(socio => {

                // console.log("Socio recibido:", socio);  // 
                // console.log("Actividad recibida:", socio.actividad); //

                const parseLocalDate = (fechaStr) => {
                    if (!fechaStr) return null;
                    const [year, month, day] = fechaStr.split('-').map(Number);
                    return new Date(year, month - 1, day, 12, 0, 0); // mes en JS es 0-based
                };

                const fechaVto = parseLocalDate(socio.fechaVencimiento);

                let diasRestantes = null;

                const diasEntre = (fechaVtoStr) => {
                    if (!fechaVtoStr) return null;

                    const [y, m, d] = fechaVtoStr.split('-').map(Number);

                    const fechaVto = Date.UTC(y, m - 1, d);
                    const hoy = new Date();
                    const hoyUTC = Date.UTC(
                        hoy.getUTCFullYear(),
                        hoy.getUTCMonth(),
                        hoy.getUTCDate()
                    );

                    return Math.floor((fechaVto - hoyUTC) / (1000 * 60 * 60 * 24));
                };
                diasRestantes = diasEntre(socio.fechaVencimiento);

                const ultIngreso = socio.ultIngreso ? new Date(socio.ultIngreso) : null;

                // Mostrar datos
                document.getElementById('datos-socio').innerHTML = `
            <p><strong>Nombre:</strong> ${socio.nombreCompleto}</p>
            <p><strong>Actividad:</strong> ${socio.actividad ? socio.actividad.nombre : 'N/A'}</p>
            <p><strong>Frecuencia:</strong> ${socio.vecesIngresado || 0} veces ingresado</p>
            <p><strong>Vencimiento:</strong> ${fechaVto ? fechaVto.toLocaleDateString('es-AR') : 'N/A'}</p>
          `;

                document.getElementById('tarjeta').style.display = 'flex';

                // --- ESTADO CUOTA ---
                const estadoDiv = document.getElementById('estado');
                estadoDiv.style.display = 'block';

                const tarjeta = document.getElementById('tarjeta');

                // BORRO botón anterior si existe
                const btnPrevio = document.getElementById('boton-abonar');
                if (btnPrevio) btnPrevio.remove();

                // Prioridad: vencida -> próximo (≤5 días) -> al día
                // Además considerar `socio.cuotaPaga === false` como vencida aunque queden días
                if (socio.cuotaPaga === false || (diasRestantes !== null && diasRestantes <= 0)) {
                    // 0 días → HOY es el vencimiento → se considera vencida
                    estadoDiv.innerHTML = '<h3>CUOTA VENCIDA</h3>';
                    estadoDiv.className = 'estado cuota-vencida';

                     // Sonido cuota vencida
                    sonidoVencida.currentTime = 0;
                    sonidoVencida.play();

                    // *** AGREGAR BOTÓN ***
                    const btn = document.createElement('a');
                    btn.id = 'boton-abonar';
                    btn.href = `/socios/abonarCuota/${socio.id}`;
                    btn.textContent = "ABONAR CUOTA";
                    btn.className = "btn-abonar";

                    estadoDiv.appendChild(btn);

                } else if (diasRestantes !== null && diasRestantes <= 5) {
                    // Entre 1 y 5 días → advertencia
                    estadoDiv.innerHTML =
                        `<h3>LA CUOTA VENCE EN ${diasRestantes} DÍA${diasRestantes === 1 ? '' : 'S'}</h3>`;
                    estadoDiv.className = 'estado cuota-proximo';
                     //  Sonido cuota proxima a vencer
                    sonidoOk.currentTime = 0;
                    sonidoOk.play();

                } else {
                    estadoDiv.innerHTML = '<h3>CUOTA AL DÍA</h3>';
                    estadoDiv.className = 'estado cuota-al-dia';

                    //  Sonido cuota al día
                    sonidoOk.currentTime = 0;
                    sonidoOk.play();
                }
                console.log("Fecha vto:", socio.fechaVencimiento, "→ Días restantes:", diasRestantes, "Fecha HOY:", socio.ultIngreso);
                console.log("Fecha recibida del backend:", socio.fechaVencimiento);
                document.getElementById('alertas').innerHTML = '';

                // -----------------------------
                // AUTO RESET A LOS 15 SEGUNDOS
                // -----------------------------
                if (timerLimpieza) {
                    clearTimeout(timerLimpieza);
                }

                timerLimpieza = setTimeout(() => {
                    // Limpiar tarjeta
                    document.getElementById('tarjeta').style.display = 'none';
                    document.getElementById('datos-socio').innerHTML = '';

                    // Limpiar estado
                    document.getElementById('estado').style.display = 'none';
                    document.getElementById('estado').innerHTML = '';

                    // Limpiar alertas
                    document.getElementById('alertas').innerHTML = '';

                    // Limpiar input y devolver foco
                    const input = document.getElementById('busqueda');
                    input.value = '';
                    input.focus();
                }, 10000);
            })
            .catch(() => {
                document.getElementById('alertas').innerHTML = '<div class="error">❌ Socio no encontrado</div>';
                document.getElementById('tarjeta').style.display = 'none';
                document.getElementById('estado').style.display = 'none';
                document.getElementById('busqueda').focus();
            });
    }
});