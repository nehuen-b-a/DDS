function crearModal(contexto, mensaje) {
    const modalHtml = `
        <div class="modal fade" id="customModal" role="dialog" aria-labelledby="customModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="customModalLabel">Confirmación de ${contexto}</h5>
                        <button type="button" class="modal-close position-absolute top-0 end-0 me-2 close" data-dismiss="modal" aria-label="Close">
                            <span class="iconify" data-icon="mdi:remove" data-width="18" data-height="18"></span>
                        </button>
                    </div>
                    <div class="modal-body">
                        ${mensaje}
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
    });

    document.getElementById('customModal').addEventListener('hidden.bs.modal', function () {
        document.getElementById('customModal').remove();
        document.getElementById('file-upload').value = '';
    });
}

function openModal(contexto, tipoEntidad) {
    const entidades = [
        {
            tipo: 'personaVulnerable',
            mensaje: '¿Estás seguro de que deseas eliminar esta persona?',
        },
        {
            tipo: 'tecnico',
            mensaje: '¿Estás seguro de que deseas eliminar este técnico?',
        },
        {
            tipo: 'cargaMasiva',
            mensaje: '¿Estás seguro de que quieres realizar la carga masiva?',
        }
    ];

    const entidad = entidades.find(e => e.tipo === tipoEntidad);

    if (!entidad) {
        console.error('Tipo de entidad no válido:', tipoEntidad);
        return;
    }

    try {
    } catch (error) {
        console.error('Error al crear el modal:', error);
    }
    crearModal(contexto, entidad.mensaje);
}