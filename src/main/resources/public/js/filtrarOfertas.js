function mostrarRubros() {
    document.getElementById('otros-rubros').style.display = 'block';
}

function filtrarOfertas(rubro) {
    const ofertas = document.querySelectorAll('.offer-card');
    ofertas.forEach(oferta => {
        if (oferta.getAttribute('data-rubro') === rubro) {
            oferta.style.display = 'block';
        } else {
            oferta.style.display = 'none';
        }
    });
}