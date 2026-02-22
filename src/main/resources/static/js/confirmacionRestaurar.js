function confirmarRestauracion(url) {
  Swal.fire({
    title: '¿RESTURAR SOCIO?',
    text: 'El socio volverá a estar activo',
    icon: 'question',

    background: 'rgba(0,0,0,0.95)',
    color: '#ffffff',

    iconColor: '#00ff9c',

    showCancelButton: true,
    confirmButtonText: 'RESTAURAR',
    cancelButtonText: 'CANCELAR',

    confirmButtonColor: '#00ff9c',
    cancelButtonColor: '#ff005d',

    customClass: {
      popup: 'swal-neon',
      title: 'swal-title-neon',
      icon: 'swal-icon-neon', // 👈 IMPORTANTE
      confirmButton: 'swal-btn-confirm',
      cancelButton: 'swal-btn-cancel'
    }
  }).then((result) => {
    if (result.isConfirmed) {
      window.location.href = url;
    }
  });
}