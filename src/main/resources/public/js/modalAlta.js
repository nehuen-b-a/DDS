function crearModal(mensaje, accion, advertencia) {
    const modalExistente = document.getElementById('customModal');
    if (modalExistente) {
        modalExistente.remove();
    }

    const modalHtml = `
        <div class="modal fade" id="customModal" role="dialog" aria-labelledby="customModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="customModalLabel">Confirmación de donación de vianda</h5>
                        <button type="button" class="modal-close position-absolute top-0 end-0 me-2 close" data-dismiss="modal" aria-label="Close">
                            <span class="iconify" data-icon="mdi:remove" data-width="18" data-height="18"></span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <p>${mensaje}</p>
                        <p class="warning fw-500">Advertencia: ${advertencia}</p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="modal-button" data-dismiss="modal">Cancelar</button>
                        <form id="form" method="POST" action='${accion}'>
                            <button type="submit" class="modal-button" id="confirm-submit">Sí, confirmo</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    `;

    document.body.insertAdjacentHTML('beforeend', modalHtml);
    const modal = new bootstrap.Modal(document.getElementById('customModal'));
    modal.show();

    document.getElementById('customModal').addEventListener('hidden.bs.modal', function () {
        // Eliminar el modal del DOM cuando se cierre
        document.getElementById('customModal').remove();
    });

    document.getElementById('confirm-submit').addEventListener('click', function () {
        document.getElementById('uploadForm').submit();
        modal.hide();
    });
}

function openModal(tipoEntidad) {
    const entidades = [
        {
            tipo: 'donacionVianda',
            mensaje: `¿Estás seguro de que dar de alta la donación vianda?`,
            advertencia: "Tenés 3 horas para ingresar la vianda en la heladera.",
            accion: `/donacionVianda`
        },
        {
            tipo: 'retiroDistibucionVianda',
            mensaje: `¿Estás seguro de que deseas dar de alta la distribucion vianda?`,
            advertencia: 'Tendrá 3 horas para retirar las viandas del origen.',
            accion: `/distribucionVianda`
        },
        {
            tipo: 'depositoDistribucionVianda',
            mensaje: `¿Estás seguro de que deseas dar de alta la distribucion vianda?`,
            advertencia: 'Tendrá 3 horas para depositar las viandas en el destino.',
            accion: `/distribucionVianda`
        }
    ];

    const entidad = entidades.find(e => e.tipo === tipoEntidad);

    if (!entidad) {
        console.error('Tipo de entidad no válido:', tipoEntidad);
        return;
    }

    try {
        crearModal(entidad.mensaje, entidad.accion, entidad.advertencia);
    } catch (error) {
        console.error('Error al crear el modal:', error);
    }
}
