var scene, renderer;

var currentCamera, mainCamera, frontalCamera, topCamera, lateralCamera;

var geometry, material, mesh;

var cubes, curves;
var circle;

const action_keys = ["q", "w", "a", "s", "z", "x", "ArrowLeft", "ArrowRight", "ArrowUp", "ArrowDown", "d", "c"];
const pressed_keys = new Set([]);

// compositions
var compositionX, composition1, composition2, composition3;

function createObject(x, y, z, rot, type, c, ...geometryArgs) {
    let object = new THREE.Object3D();

    // create mesh
    let material = new THREE.MeshBasicMaterial({color: c, wireframe: true});
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
        // ...
        default:
            console.log("Error: geometry type not valid!");
            return;
    }
    // create object
    object.add(new THREE.Mesh(geometry, material));
    object.position.set(x, y, z);
    if (rot) object.rotation.setFromVector3(rot);
    return object;
}

// X: Saturn & asteroid
function createCompositionX(x, y, z, rot = new THREE.Vector3(0, 0, 0)) {

    compositionX = new THREE.Group();

    let planet = new THREE.Group();
    let sphere = createObject(0, 0, 0, null, 'Sphere', 0xd7907b, 6, 15, 15);
    sphere.add(new THREE.Mesh(new THREE.SphereGeometry(6, 15, 15, 0, 2*Math.PI, 0, Math.PI/8),
                              new THREE.MeshBasicMaterial({c: 0xffffff, wireframe: true})));
    planet.add(sphere);
    let ring = new THREE.Group();
    ring.add(createObject(0, 0, 0, new THREE.Vector3(Math.PI /2, 0, 0), 'Torus',
        0xffffff0, 10.8, .8, 8 , 20));
    let moon = new THREE.Group();
    moon.add(createObject(0, 0, 0, null, 'Sphere', 0xb3679b, 3, 12, 12));
    moon.add(createObject(2.12, 2.12, 0, new THREE.Vector3(0, 0, 3*Math.PI/4),
                    'Cylinder', 0x153b50, 0, 1.85, 6));
    moon.position.set(10.4, 0, 0);
    ring.add(moon);
    planet.add(ring);
    planet.userData = {timer: 0, radius: 6, torus_distance: 10};

    let asteroid = createObject(27, 30, 7, null, 'Sphere', 0x6c4b5e, 2, 5, 5);
    asteroid.userData = {timer: 0, radius: 2, velocity: 17,
        initPosition: new THREE.Vector3(27, 30, 7)};

    compositionX.add(planet);
    compositionX.add(asteroid);

    compositionX.position.set(x, y, z);
    compositionX.rotation.setFromVector3(rot);
    scene.add(compositionX);
}

// 1: Two balls colliding
function createComposition1(x, y, z, rot = new THREE.Vector3(0, 0, 0)) {

    composition1 = new THREE.Group();

    let ball1 = createObject(80, 0, 0, null, 'Sphere', 0xff0000, 4, 10, 10);
    ball1.userData = {up: false, radius: 4};

    let ball2 = createObject(-80, 0, 0, null, 'Sphere', 0x00ff00, 4, 10, 10);
    ball2.userData = {up: true, radius: 4};

    composition1.add(ball1);
    composition1.add(ball2);

    composition1.position.set(x, y, z);
    composition1.rotation.setFromVector3(rot);
    scene.add(composition1);
}

// 2: Two torus rolling
function createComposition2(x, y, z, rot = new THREE.Vector3(0, 0, 0)) {

    composition2 = new THREE.Group();

    let torus1 = createObject(-2, 0, 0, null, 'Torus', 0xffff00, 10, 2, 16, 25);
    torus1.children[0].lookAt(new THREE.Vector3(0, 0, 1));

    let torus2 = createObject(2, 0, 0, null, 'Torus', 0xffff00, 10, 2, 16, 25);
    torus2.children[0].lookAt(new THREE.Vector3(0, 1, 0));

    composition2.add(torus1);
    composition2.add(torus2);

    composition2.position.set(x, y, z);
    composition2.rotation.setFromVector3(rot);
    scene.add(composition2);
}

// 3: Torus knot
function createComposition3(x, y, z, rot = new THREE.Vector3(0, 0, 0)) {

    composition3 = new THREE.Group();

    let specialTorus = createObject(0, 0, 0, null, 'TorusKnot', 0xffffff, 10, 3, 100, 3);
    let mesh = specialTorus.children[0];
    mesh.lookAt(new THREE.Vector3(0, 0, 1));
    mesh.material = new THREE.MeshNormalMaterial({wireframe: true});

    composition3.add(specialTorus);

    composition3.position.set(x, y, z);
    composition3.rotation.setFromVector3(rot);
    scene.add(composition3);
}

function addCubes(x, y, z, num=1){

    cubes = new THREE.Group();

    for (; num > 0; num--) {
        let cube = createObject(Math.random() * 160 - 80, Math.random() * 160 - 80, Math.random() * 160 - 80,
            null, 'Box', 0xffffff, 6, 6, 6);
        cube.children[0].material = new THREE.MeshNormalMaterial({wireframe: true});
        cubes.add(cube);
    }
    cubes.userData = {timer: 0, rot: new THREE.Vector3(8, 7.5, 0)};
    cubes.position.set(x, y, z);
    scene.add(cubes);
}

class CustomSinCurve extends THREE.Curve {
    constructor(scale, param) {
        super();
        this.scale = scale;
        if (param === 0) {
            this.r = Math.random() * 3;
            this.rr = Math.random() * 4;
            if (this.r < 1) this.r = 1
            if (this.rr < 1) this.rr = 1
        } else {
            this.r = 2;
            this.rr = 3;
        }
    }
    getPoint(t) {
        let tx = t * this.rr - 1.5;
        let ty = Math.sin(this.r * Math.PI * t);
        let tz = 0;
        return new THREE.Vector3(tx, ty, tz).multiplyScalar(this.scale);
    }
}

function createCurve(x, y, z, len, c, p = 0) {
    let path = new CustomSinCurve(len, p);
    let tubularSegments = 200;
    let radius = 1;
    let radialSegments = 8;
    let closed = false;

    let curve = createObject(x, y, z, new THREE.Vector3(Math.PI / 4, Math.PI / 4, 0),
                            'Tube', c, path, tubularSegments, radius, radialSegments, closed);
    curves.add(curve);
}

function addCurves(x, y, z, num_random_curves=0) {

    curves = new THREE.Group();

    for (; num_random_curves > 0; num_random_curves--) {
        createCurve(Math.random() * 180 - 90, Math.random() * 180 - 90, Math.random() * 180 - 90,
            Math.random() * 30 + 20, Math.random() * 0xffffff);
    }
    createCurve(-30, 0, -30, 50, 0xff0000, 1);

    cubes.position.set(x, y, z);
    scene.add(curves);
}

function addCircle(){
    'use strict';

    geometry = new THREE.CircleGeometry( 5, 32 );
    material = new THREE.MeshPhongMaterial( { color: 0xffff00, wireframe: true} );
    circle = new THREE.Mesh( geometry, material );
    circle.userData = ({approx: true});
    circle.position.x = 0;
    circle.position.y = 0;
    circle.position.z = -100;
    scene.add( circle );
}

function createScene() {
    'use strict';
    
    scene = new THREE.Scene();

    scene.add(new THREE.AxisHelper(10));

    const light = new THREE.PointLight( 0xff0000, 1, 100 );
    light.position.set(0, 0, 1);
    scene.add( light );

    addCubes(0, 0, 0, 8);
    addCurves(0, 0, 0, 4);
    addCircle();

    createCompositionX(46, -22, 36);
    createComposition1(0, 0, 0);
    createComposition2(72, 48, -22);
    createComposition3(-90, -40, -40);
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
        case "Frontal":
            currentCamera = frontalCamera;
            break;
        case "Top":
            currentCamera = topCamera;
            break;
        case "Lateral":
            currentCamera = lateralCamera;
            break;
        case "Main":
            currentCamera = mainCamera;
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
        case "1": // camara frontal
            assignCamera("Frontal");
            break;
        case "2": // camara topo
            assignCamera("Top");
            break;
        case "3": // camara lateral
            assignCamera("Lateral");
            break;
        case "4": // ativa/desativa wireframe dos objetos
            scene.traverse(function (node) {
                if (node instanceof THREE.Mesh) {
                    node.material.wireframe = !node.material.wireframe;
                }
            });
            break;
        case "5": // camara principal
            assignCamera("Main");
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

function applyKeyMovement(key, tr_v, rot_v) {
    switch(key){
        case "q":
            compositionX.children[0].rotateZ(-rot_v);
            break;
        case "w":
            compositionX.children[0].rotateZ(rot_v);
            break;
        
        case "a":
            compositionX.children[0].children[1].rotateY(-rot_v);
            break;
        case "s":
            compositionX.children[0].children[1].rotateY(rot_v);
            break;
        
        case "z":
            compositionX.children[0].children[1].children[1].rotateX(-rot_v);
            break;
        case "x":
            compositionX.children[0].children[1].children[1].rotateX(rot_v);
            break;

        case "ArrowLeft":
            compositionX.children[0].translateX(-tr_v);
            break;
        case "ArrowRight":
            compositionX.children[0].translateX(tr_v);
            break;

        case "ArrowUp":
            compositionX.children[0].translateY(tr_v);
            break;
        case "ArrowDown":
            compositionX.children[0].translateY(-tr_v);
            break;
        
        case "d":
            compositionX.children[0].translateZ(-tr_v);
            break;
        case "c":
            compositionX.children[0].translateZ(tr_v);
            break;
    }
}

function render() {
    'use strict';
    renderer.render(scene, currentCamera);
}

function init() {
    'use strict';

    renderer = new THREE.WebGLRenderer({antialias: true, alpha: true});
    renderer.setClearColor( 0xfae9a0, 0.8 );
    renderer.setSize(window.innerWidth, window.innerHeight);
    document.body.appendChild(renderer.domElement);
   
    createScene();

    let aspect = window.innerWidth / window.innerHeight;
    frontalCamera = createCamera(0, 0, 100, 'Orthographic', -60*aspect, 60*aspect, 60, -60, -500, 500);
    topCamera = createCamera(0, 100, 0, 'Orthographic', -60*aspect, 60*aspect, 60, -60, -500, 500);
    lateralCamera = createCamera(100, 0, 0, 'Orthographic', -60*aspect, 60*aspect, 60, -60, -500, 500);
    mainCamera = createCamera(0, 0, 100, 'Perspective', 70, aspect, 1, 1000);

    assignCamera("Main");

    render();
    
    window.addEventListener("keydown", onKeyDown);
    window.addEventListener("keyup", onKeyUp);
    window.addEventListener("resize", onResize);
}

function updateCubes(delta) {
    cubes.children.forEach(function (cube) {
        cube.rotation.x += cubes.userData.rot.x * delta;
        cube.rotation.y += cubes.userData.rot.y * delta;
        cube.rotation.z += cubes.userData.rot.z * delta;
    });
}


// X: Saturn & asteroid
function updateCompositionX(delta) {
    let planet = compositionX.children[0];
    let asteroid = compositionX.children[1];
    let distVector = new THREE.Vector3();

    distVector.subVectors(planet.position, asteroid.position);
    let distance = distVector.length();
    let direction = distVector.normalize();
    asteroid.position.add(direction.multiplyScalar(asteroid.userData.velocity * delta));

    if (distance <= planet.userData.radius + asteroid.userData.radius) {
        planet.children[1].children[1].children[0].children[0].material.color.setHex(Math.random() * 0xffffff);

        // reset asteroid position
        asteroid.position.x = asteroid.userData.initPosition.x;
        asteroid.position.y = asteroid.userData.initPosition.y;
        asteroid.position.z = asteroid.userData.initPosition.z;
    }
}

// 1: Two balls colliding
function updateComposition1(delta){
    let ball1 = composition1.children[0];
    let ball2 = composition1.children[1];

    if (!ball1.up && ball1.position.x - ball1.userData.radius > 0) {
        ball1.position.x -= delta;
    } else if (!ball1.up) {
        ball1.up = true;
    } else if (ball1.up && ball1.position.x < 80) {
        ball1.position.x += delta;
    } else {
        ball1.up = false;
    }

    if (!ball2.up && ball2.position.x > -80) {
        ball2.position.x -= delta;
    } else if (!ball2.up) {
        ball2.up = true;
    } else if (ball2.up && ball2.position.x + ball2.userData.radius < 0) {
        ball2.position.x += delta;
    } else {
        ball2.up = false;
    }
}

// 2: Two torus rolling
function updateComposition2(delta){
    composition2.children[0].rotateX(delta);
    composition2.children[1].rotateZ(delta);
}

// 3: Torus knot
function updateComposition3(delta) {
    composition3.rotateX(delta);
}

function moveCircle(){
    if (circle.approx && circle.position.z < 100) {
        circle.position.z += 0.2;
    } else if (circle.approx) {
        circle.approx = false;
    } else if (!circle.approx && circle.position.z > -100) {
        circle.position.z -= 0.2;
    } else {
        circle.approx = true;
    }

    if(circle.position.z > 60) {
        circle.rotateZ(-0.03);
    } else if (circle.position.z > 20) {
        circle.rotateZ(-0.02);
    } else {
        circle.rotateZ(-0.01);
    }
}

function animate() {
    'use strict';

    pressed_keys.forEach(function (key) {applyKeyMovement(key, 1, 0.02);});

    updateCubes(0.001);

    // update compositions
    updateCompositionX(0.005);
    updateComposition1(0.4);
    updateComposition2(0.01);
    updateComposition3(0.02);

    moveCircle();

    render();
    requestAnimationFrame(animate);
}
