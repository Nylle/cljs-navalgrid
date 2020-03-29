function createMap(container, zoom, center) {
    return new mapboxgl.Map({
        container: container,
        style: 'style.json',
        center: center,
        zoom: zoom
    });
}