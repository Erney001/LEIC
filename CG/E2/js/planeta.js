var scene, renderer;
var currentCamera, frontalOrtCamera, frontalPerCamera, rocketCamera;
var timer = 0;

const action_keys = ["ArrowLeft", "ArrowRight", "ArrowUp", "ArrowDown"];
const pressed_keys = new Set([]);

const debrisType = ["Box", "Cone", "Dodecahedron", "Icosahedron", "Octahedron", "Tetrahedron"];
const planetRadius = 200;

var planet, rocket, spaceDebris;
let textureLoader = new THREE.TextureLoader();
textureLoader.crossOrigin = true;

function getRandomInt(min, max) {
    min = Math.ceil(min);
    max = Math.floor(max);
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function createObject(x, y, z, rot, type, c, ...geometryArgs) {

    let object = new THREE.Object3D();

    let material = new THREE.MeshBasicMaterial({color: c});
    let geometry;

    switch (type) {
        case 'Sphere':
            geometry = new THREE.SphereGeometry(...geometryArgs);
            break;
        case 'Torus':
            geometry = new THREE.TorusGeometry(...geometryArgs);
            break;
        case 'TorusKnot':
            geometry = new THREE.TorusKnotGeometry(...geometryArgs);
            break;
        case 'Box':
            geometry = new THREE.BoxGeometry(...geometryArgs);
            break;
        case 'Cylinder':
            geometry = new THREE.CylinderGeometry(...geometryArgs);
            break;
        case 'Tube':
            geometry = new THREE.TubeGeometry(...geometryArgs);
            break;
        case 'Capsule':
            geometry = new THREE.CapsuleGeometry(...geometryArgs);
            break;
        case 'Cone':
            geometry = new THREE.ConeGeometry(...geometryArgs);
            break;
        case 'Dodecahedron':
            geometry = new THREE.DodecahedronGeometry(...geometryArgs);
            break;
        case 'Icosahedron':
            geometry = new THREE.IcosahedronGeometry(...geometryArgs);
            break;
        case 'Octahedron':
            geometry = new THREE.OctahedronGeometry(...geometryArgs);
            break;
        case 'Tetrahedron':
            geometry = new THREE.TetrahedronGeometry(...geometryArgs);
            break;
        default:
            console.log("Error: geometry type not valid!");
            return;
    }

    object.add(new THREE.Mesh(geometry, material));
    object.position.set(x, y, z);
    if (rot) object.rotation.setFromVector3(rot);

    return object;
}

function addBoundary(object, x, y, z, radius, color, name) {
    let boundingSphere = createObject(x, y, z, null, 'Sphere', color, radius, 20, 20);
    let points = new THREE.Group();
    points.add(
        createPoint(radius, Math.PI/4, 3*Math.PI/4),
        createPoint(radius,Math.PI/4, 5*Math.PI/4),
        createPoint(radius,Math.PI/4, 7*Math.PI/4),
        createPoint(radius,Math.PI/4, Math.PI/4),
        createPoint(radius,3*Math.PI/4, 3*Math.PI/4),
        createPoint(radius,3*Math.PI/4, 5*Math.PI/4),
        createPoint(radius,3*Math.PI/4, 7*Math.PI/4),
        createPoint(radius,3*Math.PI/4, Math.PI/4)
    );
    boundingSphere.add(points);

    boundingSphere.children[0].material.transparent = true;
    boundingSphere.children[0].material.opacity = .5;
    boundingSphere.children[0].material.visible = false;
    object.add(boundingSphere);
}

function createPlanet(x, y, z, num) {

    planet = createObject(x, y, z, null, 'Sphere', 0xFCF7FF, planetRadius, 50, 50);

    planet.children[0].material = new THREE.MeshPhongMaterial({
        bumpScale: 1.2*planetRadius,
        specular: new THREE.Color('grey'),
        shininess: 10,
        color: 0xFCF7FF,
        map: textureLoader.load('https://i.ibb.co/KDvqzKL/earthmap1k.jpg'),
        bumpMap: textureLoader.load('https://i.ibb.co/dQSzbYp/earthbump1k.jpg'),
        specularMap: textureLoader.load('https://i.ibb.co/Qr4BvCy/earthspec1k.jpg'),
    });
    let spotlight = new THREE.SpotLight(0xffffff, 1.2, 1000, Math.PI/4, 1, 0);
    spotlight.position.set(1.4*planetRadius, 1.4*planetRadius, 0);
    planet.add(spotlight, new THREE.AmbientLight(0xffffff, 1.15));

    planet.position.set(x, y, z);
    scene.add(planet);

    spaceDebris = new THREE.Group();

    let i = 1000;
    for (; num > 0; num--) {
        const wrapper = new THREE.Group();
        wrapper.position.set(x, y, z);
        wrapper.rotation.set(6 * Math.random(), 6 * Math.random(), 6 * Math.random());

        let type = debrisType[Math.floor(Math.random() * debrisType.length)];
        let radius = getRandomInt(planetRadius/24, planetRadius/20);
        let color = Math.random() * 0xffffff;
        let detail = (["Dodecahedron", "Icosahedron", "Octahedron", "Tetrahedron"].includes(type))? 0: 5;

        let debris = createObject(0, 0, 0, null, type, color, radius, detail, 5);
        addBoundary(debris, 0, 0, 0, radius, 0xffffff, "asteroid" + i++);

        debris.children[0].translateY(1.20*planetRadius);  // set radius of debris
        debris.children[1].translateY(1.20*planetRadius);  // set radius of debris

        wrapper.add(debris);

        wrapper.userData.speed = getRandomInt(1, 7) * 0.0001;
        spaceDebris.add(wrapper);
    }

    spaceDebris.position.set(x, y, z);
    scene.add(spaceDebris);
}

function createPoint(radius, phi, theta) {
    let point = new THREE.Object3D();
    point.position.setFromSphericalCoords(radius, phi, theta);
    return point;
}

function createRocket(dist=1) {

    rocket = new THREE.Group();

    let height = getRandomInt(planetRadius/12, planetRadius/10);
    let h1 = .2*height, h2 = .6*height, h3 = .2*height;
    let pieces = new THREE.Group();

    let body = createObject(0, 0, 0, null, 'Cylinder', 0xcc0000, .25*height, .25*height, h2);
    addBoundary(body, 0, .25*height*.5, 0, .25*height, 0xffffff, "rocketbody");
    addBoundary(body, 0, -.25*height*.5, 0, .25*height, 0xffffff, "rocketbody");
    let head = createObject(0, h2/2 + h3/2, 0, null,'Cylinder', 0xFCF7FF, 0, .25*height, h3);
    addBoundary(head, 0, 0, 0, .25*height, 0xffffff, "rockethead");
    let prop1 = createObject(-.25*height, -h2/2, 0, null, 'Capsule', 0xffff00, .1*height, h1);
    addBoundary(prop1, 0, .3*h1, 0, .1*height, 0xffffff, "rocketprop1");
    addBoundary(prop1, 0, 0, 0, .1*height, 0xffffff, "rocketprop1");
    addBoundary(prop1, 0, -.3*h1, 0, .1*height, 0xffffff, "rocketprop1");
    let prop2 = createObject(.25*height, -h2/2, 0, null, 'Capsule', 0xffff00, .1*height, h1);
    addBoundary(prop2, 0, .3*h1, 0, .1*height, 0xffffff, "rocketprop2");
    addBoundary(prop2, 0, 0, 0, .1*height, 0xffffff, "rocketprop2");
    addBoundary(prop2, 0, -.3*h1, 0, .1*height, 0xffffff, "rocketprop2");
    let prop3 = createObject(0, -h2/2, -.25*height, null, 'Capsule', 0xffff00, .1*height, h1);
    addBoundary(prop3, 0, .3*h1, 0, .1*height, 0xffffff, "rocketprop3");
    addBoundary(prop3, 0, 0, 0, .1*height, 0xffffff, "rocketprop3");
    addBoundary(prop3, 0, -.3*h1, 0, .1*height, 0xffffff, "rocketprop3");
    let prop4 = createObject(0, -h2/2, .25*height, null, 'Capsule', 0xffff00, .1*height, h1);
    addBoundary(prop4, 0, .3*h1, 0, .1*height, 0xffffff, "rocketprop4");
    addBoundary(prop4, 0, 0, 0, .3*h1, 0xffffff, "rocketprop4");
    addBoundary(prop4, 0, -.3*h1, 0, .1*height, 0xffffff, "rocketprop4");

    pieces.add(body, head, prop1, prop2, prop3, prop4);

    rocket.add(pieces);

    rocketCamera = createCamera(0, -.75*height, -1.65*height,
        'Perspective', 60, window.innerWidth / window.innerHeight, 1, 1000);
    rocketCamera.name = "camera";

    rocket.add(rocketCamera);

    let spotlight = new THREE.SpotLight(0xffffff, .55, planetRadius, Math.PI/2, 1, 0);
    spotlight.position.set(0, 0, .1*planetRadius);
    rocket.add(spotlight);

    rocket.userData = {phi: THREE.MathUtils.degToRad(getRandomInt(0, 180)),
                       theta: THREE.MathUtils.degToRad(getRandomInt(0, 360)),
                       velocity: 0.0007};

    rocket.position.setFromSphericalCoords(dist, rocket.userData.phi, rocket.userData.theta);
    rocket.lookAt(0, 0, 0);

    scene.add(rocket);
}

function createScene() {
    'use strict';

    scene = new THREE.Scene();

    scene.add(new THREE.AxisHelper(200));

    let imageBG = textureLoader.load('https://i.ibb.co/0fYwrhp/stars.jpg');
    scene.add(new THREE.Mesh(
        new THREE.SphereGeometry(1.25*planetRadius, 32, 32),
        new THREE.MeshBasicMaterial({
            side: THREE.BackSide,
            map: imageBG
        })));
    scene.background = imageBG;

    createPlanet(0, 0, 0, 20);
    createRocket(1.20 * planetRadius);
}

function createCamera(x, y, z, type, ...cameraArgs) {
    let camera;
    switch (type) {
        case 'Orthographic':
            camera = new THREE.OrthographicCamera(...cameraArgs);
            break;
        case 'Perspective':
            camera = new THREE.PerspectiveCamera(...cameraArgs);
            break;
        default:
            console.log("Error: camera type not valid!");
            return;
    }
    camera.position.set(x, y, z);
    camera.lookAt(scene.position);
    return camera;
}

function assignCamera(type) {
    switch (type) {
        case "FrontalOrt":
            currentCamera = frontalOrtCamera;
            rocketCamera.userData.on = false;
            break;
        case "FrontalPer":
            currentCamera = frontalPerCamera;
            rocketCamera.userData.on = false;
            break;
        case "Rocket":
            currentCamera = rocketCamera;
            rocketCamera.userData.on = true;
            break;
        default:
            console.log("Error: camera type not valid!");
            return
    }
    render();
}

function onResize() {
    'use strict';

    renderer.setSize(window.innerWidth, window.innerHeight);

    if (window.innerHeight > 0 && window.innerWidth > 0) {
        currentCamera.aspect = window.innerWidth / window.innerHeight;
        currentCamera.updateProjectionMatrix();
    }
}

function onKeyDown(e) {
    'use strict';

    switch (e.key) {
        case "1": // camara frontal ortogonal fixa
            assignCamera("FrontalOrt");
            break;
        case "2": // camara frontal perspetiva fixa
            assignCamera("FrontalPer");
            break;
        case "3": // camara fogetao perspetiva movel
            assignCamera("Rocket");
            break;
        case "4":
            rocket.children[0].children.forEach(function (part) {
                part.children.forEach(function (boundingSphere, index) {
                    if (index === 0) return;

                    boundingSphere.children[0].material.visible =
                        !boundingSphere.children[0].material.visible;
                })
                part.children[0].visible = ! part.children[0].visible;
            });
            spaceDebris.children.forEach(function (debris) {
                debris.children[0].children[1].children[0].material.visible =
                    ! debris.children[0].children[1].children[0].material.visible;
                debris.children[0].children[0].visible = ! debris.children[0].children[0].visible;
            });
            break;

        default:
            if (action_keys.includes(e.key)) {
                pressed_keys.add(e.key);
            }
    }
}

function onKeyUp(e){
    'use strict';

    if (!action_keys.includes(e.key)) return;

    pressed_keys.delete(e.key);
}

function applyKeyMovement(key, delta=0) {
    switch (key) {
        case "ArrowLeft":
            rocket.userData.theta -= rocket.userData.velocity * delta;
            break;
        case "ArrowRight":
            rocket.userData.theta += rocket.userData.velocity * delta;
            break;

        case "ArrowUp":
            rocket.userData.phi -= rocket.userData.velocity * delta;
            break;
        case "ArrowDown":
            rocket.userData.phi += rocket.userData.velocity * delta;
            break;
    }
    let theta = rocket.userData.theta;
    let phi = rocket.userData.phi;
    rocket.up = new THREE.Vector3(
        1.20 * planetRadius * Math.sin(theta) * Math.sin(phi),
        1.20 * planetRadius * Math.cos(phi),
        1.20 * planetRadius * Math.cos(theta) * Math.sin(phi)
    ).normalize();
    rocket.lookAt(0, 0, 0);
    rocket.position.setFromSphericalCoords(1.20*planetRadius, rocket.userData.phi, rocket.userData.theta);
}

function render() {
    'use strict';
    renderer.render(scene, currentCamera);
}

function init() {
    'use strict';

    renderer = new THREE.WebGLRenderer({antialias: true, alpha: true});
    renderer.setClearColor( 0x000000, 1 );
    renderer.setSize(window.innerWidth, window.innerHeight);
    document.body.appendChild(renderer.domElement);

    createScene();

    let aspect = window.innerWidth / window.innerHeight;
    frontalPerCamera = createCamera(0, 0, 3*planetRadius, 'Perspective', 60, aspect, 1, 1000);
    frontalOrtCamera = createCamera(0, 0, 3*planetRadius, 'Orthographic', -1.5*planetRadius*aspect,
        1.5*planetRadius*aspect, 1.5*planetRadius, -1.5*planetRadius, 1, 5*planetRadius);

    assignCamera("FrontalOrt");

    render();

    window.addEventListener("keydown", onKeyDown);
    window.addEventListener("keyup", onKeyUp);
    window.addEventListener("resize", onResize);
}

function moveSpaceDebris(delta){
    spaceDebris.children.forEach(function (debris) {
        debris.children[0].rotation.x += debris.userData.speed * delta;
    });
}

function reactionCollisions(collisions){
    let to_remove = [];

    collisions.forEach(function(debris){
        to_remove.push(debris);
    });

    to_remove.forEach(function(debris){
        spaceDebris.remove(debris);
    });
}

function verifyDistanceToRocket(debris){
    let debrisRadius = debris.children[0].children[1].children[0].geometry.parameters.radius;

    for (let component of rocket.children[0].children) {
        let componentRadius = component.children[1].children[0].geometry.parameters.radius;

        let debrisWP = debris.children[0].children[0].getWorldPosition(new THREE.Vector3(scene.position));
        let rocketPos = new THREE.Vector3();
        rocket.getWorldPosition(rocketPos);

        let dist = debrisWP.distanceTo(rocketPos);

        if (dist <= debrisRadius + componentRadius) {
            return true;
        }
    }
    return false;
}

function signOf(n) {
    return 2*(n >= 0) - 1;
}

function checkSameOctantAsRocket(debris, rocketOctants) {
    for (let point of debris.children[0].children[1].children[1].children) {
        let pointWP = point.getWorldPosition(new THREE.Vector3(scene.position));
        let octant = [signOf(pointWP.x), signOf(pointWP.y), signOf(pointWP.z)].toString();

        if (rocketOctants.has(octant)) {
            return true;
        }
    }
    return false;
}

function verifyCollisions(){
    // get current rocket octants
    let octants = new Set();
    rocket.children[0].children.forEach(function (part) {
        part.children[1].children[1].children.forEach(function (point) {
            let pointWP = point.getWorldPosition(new THREE.Vector3(scene.position));
            let octant = [signOf(pointWP.x), signOf(pointWP.y), signOf(pointWP.z)].toString();
            if (!octants.has(octant)) {
                octants.add(octant);
            }
        });
    });

    let collisions = [];
    spaceDebris.children.forEach(function (debris) {
        if (checkSameOctantAsRocket(debris, octants)
            && verifyDistanceToRocket(debris)) {
            collisions.push(debris);
        }
    });

    reactionCollisions(collisions);
}

function animate() {
    'use strict';

    let delta = Date.now() - timer;

    pressed_keys.forEach(function (key) {applyKeyMovement(key, delta);});

    verifyCollisions();

    moveSpaceDebris(delta);

    timer += delta;

    render();
    requestAnimationFrame(animate);
}