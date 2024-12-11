function canjearOferta(id) {
    fetch('/ofertas/canjearOferta', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ idOferta: id }) // Aquí envías el ID
    })
        .then(response => {
            if (!response.ok) throw new Error('Error en el canje');
            return response.json(); // O response.text() si esperas un texto
        })
        .then(data => {
            alert('Canje exitoso');
            // Aquí puedes hacer cualquier actualización en la interfaz de usuario
        })
        .catch(error => {
            alert(error.message);
        });
}
