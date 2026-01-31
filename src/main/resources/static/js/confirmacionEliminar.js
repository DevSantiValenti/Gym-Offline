function confirmarEliminacion(url) {
  Swal.fire({
    title: '¿Estas seguro?',
    text: 'Esta acción no se puede deshacer',
    icon: 'warning',

    background: 'rgba(0,0,0,0.95)',
    color: '#ffffff',

    iconColor: '#ff005d',

    showCancelButton: true,
    confirmButtonText: 'ELIMINAR',
    cancelButtonText: 'CANCELAR',

    confirmButtonColor: '#ff005d',
    cancelButtonColor: '#00ff9c',

    customClass: {
      popup: 'swal-neon',
      title: 'swal-title-neon',
      confirmButton: 'swal-btn-confirm',
      cancelButton: 'swal-btn-cancel'
    }
  }).then((result) => {
    if (result.isConfirmed) {
      window.location.href = url;
    }
  });
}
