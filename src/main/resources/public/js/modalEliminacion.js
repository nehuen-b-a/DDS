function crearModal(mensaje, accion) {
    const modalExistente = document.getElementById('customModal');
    if (modalExistente) {
        modalExistente.remove();
    }

    const modalHtml = `
        <div class="modal fade" id="customModal" role="dialog" aria-labelledby="customModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="customModalLabel">Confirmación de eliminación</h5>
                        <button type="button" class="modal-close position-absolute top-0 end-0 me-2 close" data-dismiss="modal" aria-label="Close">
                            <span class="iconify" data-icon="mdi:remove" data-width="18" data-height="18"></span>
                        </button>
                    </div>
                    <div class="modal-body">
                        ${mensaje}
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
}

function openModal(idEntidad, tipoEntidad, nombreEntidad) {
    const entidades = [
        {
            tipo: 'personaVulnerable',
            mensaje: `¿Estás seguro de que deseas eliminar a ${nombreEntidad}?`,
            accion: `/personasVulnerables/${idEntidad}/eliminacion`
        },
        {
            tipo: 'tecnico',
            mensaje: `¿Estás seguro de que deseas eliminar a ${nombreEntidad}?`,
            accion: `/tecnicos/${idEntidad}/eliminacion`
        },
        {
            tipo: 'donacionDinero',
            mensaje: `¿Estás seguro de que deseas cancelar la donación de dinero?`,
            accion: `/donacionDinero/${idEntidad}/eliminacion`
        },
        {
            tipo: 'donacionVianda',
            mensaje: `¿Estás seguro de que deseas eliminar la donación de vianda?`,
            accion: `/donacionVianda/${idEntidad}/eliminacion`
        },
        {
            tipo: 'personaHumana',
            mensaje: `${nombreEntidad}, ¿Estás seguro de que deseas dar de baja tu cuenta?`,
            accion: `/personaHumana/${idEntidad}/eliminacion`
        },
        {
            tipo: 'personaJuridica',
            mensaje: `${nombreEntidad}, ¿Estás seguro de que deseas dar de baja tu cuenta?`,
            accion: `/personaJuridica/${idEntidad}/eliminacion`
        }
    ];

    const entidad = entidades.find(e => e.tipo === tipoEntidad);

    if (!entidad) {
        console.error('Tipo de entidad no válido:', tipoEntidad);
        return;
    }

    try {
        crearModal(entidad.mensaje, entidad.accion);
    } catch (error) {
        console.error('Error al crear el modal:', error);
    }
}
