mapboxgl.accessToken = 'pk.eyJ1IjoiZmdhdW5hc29tYSIsImEiOiJjbHY0NjdlcTcwNXN1Mmpsc2twNHJ4NWs2In0.1rFiIv1sJZ16-xbd4zWnyw';

var map = new mapboxgl.Map({
    container: 'map',
    style: 'mapbox://styles/mapbox/streets-v11',
    center: [-58.42011700459251, -34.5984903697883],
    zoom: 11
});
map.addControl(new mapboxgl.NavigationControl());
console.log('Mapbox loaded');

// Custom marker image URL
var customMarkerImage = '/img/favicon-light.png';

var currentMarkers = [];

// Función para agregar marcadores en el mapa
function addMarkers(heladeras, onMarkerClick) {
    heladeras.forEach(function (heladera) { addMarker(heladera, onMarkerClick) });
}

// Función para agregar un marcador existente en el mapa
function addMarker(heladera, onMarkerClick) {
    var lat = parseFloat(heladera.direccion.coordenada.latitud);
    var lng = parseFloat(heladera.direccion.coordenada.longitud);

    if (!isNaN(lat) && !isNaN(lng)) {
        var marker = new mapboxgl.Marker({
            element: createCustomMarker(customMarkerImage),
            draggable: false
        })
            .setLngLat([lng, lat])
            .addTo(map);

        if(onMarkerClick !== undefined){
            marker.getElement().addEventListener('click', onMarkerClick.bind({ "heladera": heladera }));
        }
        /*if (document.querySelector(`[data-id="${heladera.id}"]`) != undefined) {
            document.querySelector(`[data-id="${heladera.id}"]`).addEventListener('click', function () {
                map.flyTo({
                    center: [lng, lat],
                    zoom: 15
                });
            });
        }*/
    } else {
        console.log(`Coordenadas inválidas para heladera con id: ${heladera.id}`);
    }

    currentMarkers.push(marker); // Guardar el marcador
}

// Función para agregar un nuevo marcador en el mapa
// Variable para almacenar el marcador único
var singleMarker = null;

// Función para agregar o mover el marcador en el mapa
function addNewMarker(lng, lat, rutaMarker) {
    var customMarkerImage = rutaMarker;
    // Si ya existe un marcador, removerlo antes de crear uno nuevo
    if (currentMarkers.length > 0) {
        currentMarkers[0].remove();  // Remover el marcador anterior
        currentMarkers = [];  // Limpiar la lista de marcadores actuales
    }

    var newMarker = new mapboxgl.Marker({
        element: createCustomMarker(customMarkerImage),
        draggable: true // Permitir que el marcador sea arrastrable
    })
        .setLngLat([lng, lat])
        .addTo(map);

    // Evento 'dragend' para actualizar los campos de latitud y longitud al arrastrar el marcador
    newMarker.on('dragend', function () {
        var lngLat = newMarker.getLngLat();
        console.log('Marcador arrastrado a:', lngLat);

        // Actualizar los campos de latitud y longitud en el formulario
        document.getElementById('latitud').value = lngLat.lat;
        document.getElementById('longitud').value = lngLat.lng;
    });

    // Guardar el nuevo marcador en la lista de marcadores actuales
    currentMarkers.push(newMarker);

    // Actualizar los campos de latitud y longitud al crear el marcador
    document.getElementById('latitud').value = lat;
    document.getElementById('longitud').value = lng;
}

// Función para crear un marcador personalizado
function createCustomMarker(markerImage) {
    var img = document.createElement('img');
    img.src = markerImage;
    img.style.width = '35px';
    img.style.height = '35px';

    var customMarker = document.createElement('div');
    customMarker.appendChild(img);

    return customMarker;
}

function clearMarkers() {
    currentMarkers.forEach(marker => marker.remove());
    currentMarkers = [];
}

var selectedMarker = null; // Variable para almacenar el marcador actualmente seleccionado

function selectMarker(heladera, inputId) {
    // Cambia el icono del marcador previamente seleccionado, si existe
    if (selectedMarker) {
        selectedMarker.getElement().querySelector('img').src = '/img/favicon-light.png';
    }

    // Encuentra el nuevo marcador en base a la heladera seleccionada
    var marker = currentMarkers.find(m => m.getLngLat().lng === parseFloat(heladera.direccion.coordenada.longitud) &&
        m.getLngLat().lat === parseFloat(heladera.direccion.coordenada.latitud));

    if (marker) {
        // Cambia el icono del marcador seleccionado
        marker.getElement().querySelector('img').src = '/img/selected-marker.png';
        selectedMarker = marker;

        // Coloca el nombre de la heladera en el input
        document.getElementById(inputId).value = heladera.nombre;
    } else {
        console.log("Marcador no encontrado para la heladera seleccionada.");
    }
}
