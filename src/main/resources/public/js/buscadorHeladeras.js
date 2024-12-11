document.addEventListener('DOMContentLoaded', function() {
    var heladeras = window.heladeras || [];  // Asegúrate de que heladeras esté definida

    var searchInput = document.getElementById('search-input');
    if (!searchInput) {
        console.error('El campo de búsqueda no está disponible');
        return;
    }

    // Filtrar y actualizar los resultados en el sidebar y el mapa
    searchInput.addEventListener('input', function() {
        var searchTerm = searchInput.value.toLowerCase();
        var filteredHeladeras = heladeras.filter(function(heladera) {
            return heladera.nombre.toLowerCase().includes(searchTerm) ||
                heladera.direccion.nomenclatura.toLowerCase().includes(searchTerm);
        });

        // Actualizar la lista lateral
        renderHeladeras(filteredHeladeras);

        // Eliminar los marcadores actuales del mapa y agregar los filtrados
        clearMarkers();
        addMarkers(filteredHeladeras, function() {
            window.location.href = `/mapaHeladeras/${this.heladera.id}/HeladeraParticular`;
        });
    });
});

function renderHeladeras(filteredHeladeras) {
    var heladerasList = document.getElementById('heladeras-list');
    heladerasList.innerHTML = '';  // Limpiar resultados anteriores

    filteredHeladeras.forEach(function(heladera) {
        var heladeraCard = `
            <a href="/mapaHeladeras/${heladera.id}/HeladeraParticular" class="card rounded-3 transition-card mb-2 clic-card" data-id="${heladera.id}">
                <div class="card-result-body">
                    <h5 class="card-result-title">${heladera.nombre}</h5>
                    <p class="card-result-text">${heladera.direccion.nomenclatura}</p>
                    <p class="card-result-text">Tiene ${heladera.cantidadViandas} viandas</p>
                    <p class="card-result-text">Capacidad de ${heladera.capacidadMaximaViandas} viandas</p>
                </div>
            </a>
        `;
        heladerasList.insertAdjacentHTML('beforeend', heladeraCard);
    });
}
