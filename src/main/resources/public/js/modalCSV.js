document.getElementById('file-upload').addEventListener('click', function() {
    this.value = '';
});

document.getElementById('file-upload').addEventListener('change', function (event) {
    const archivoSeleccionado = event.target.files[0];
    if (archivoSeleccionado) {
        crearModal(archivoSeleccionado.name);
    }
});

function crearModal(nombreArchivo) {
    const modalHtml = `
        <div class="modal fade" id="customModal" role="dialog" aria-labelledby="customModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="customModalLabel">Confirmación de carga masiva</h5>
                        <button type="button" class="modal-close position-absolute top-0 end-0 me-2 close" data-dismiss="modal" aria-label="Close">
                            <span class="iconify" data-icon="mdi:remove" data-width="18" data-height="18"></span>
                        </button>
                    </div>
                    <div class="modal-body">
                        Estás seguro de que quieres realizar la carga masiva mediante el archivo ${nombreArchivo}?
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="modal-button" data-dismiss="modal">Cancelar</button>
                        <button type="button" class="modal-button" id="confirm-submit">Sí, confirmo</button>
                    </div>
                </div>
            </div>
        </div>
    `;

    document.body.insertAdjacentHTML('beforeend', modalHtml);
    const modal = new bootstrap.Modal(document.getElementById('customModal'));
    modal.show();

    document.getElementById('confirm-submit').addEventListener('click', function () {
        document.getElementById('form').submit();
        modal.hide();
        document.getElementById('loader').classList.add('show');
    });

    document.getElementById('customModal').addEventListener('hidden.bs.modal', function () {
        document.getElementById('customModal').remove();
        document.getElementById('file-upload').value = '';
    });
}