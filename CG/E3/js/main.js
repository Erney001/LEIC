let scene, renderer;
let currentCamera, ortCamera, perCamera, vrCamera;

const action_keys = ["q", "w", "e", "r", "t", "y"];
const pressed_keys = new Set([]);

let timer = 0;
let isPaused = false;

function createScene() {
    'use strict';
    scene = new THREE.Scene();
    scene.background = new THREE.Color(0x54577C);

    createFloor(0, 0, 0, 175, 175);
    createStand(0, 0, -4, 70, 7, 24, 70, 3, 12);
    createInitialPiece(-21, 10, 0.0);
    createIntermediatePiece(0, 10, 0);
    createSwan(21, 10, 0);

    addLight();
}

function createCamera(x, y, z, type, lookDir, ...cameraArgs) {
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
    if (lookDir)
        camera.lookAt(lookDir.add(camera.position));
    else
        camera.lookAt(scene.position);
    return camera;
}

function assignCamera(type) {
    switch (type) {
        case "Per":
            currentCamera = perCamera;
            break;
        case "Ort":
            currentCamera = ortCamera;
            break;
        case "VR":
            currentCamera = vrCamera;
            break;
        default:
            console.log("Error: a camera with that type doesn't exit!");
            return
    }
    render();
}

function resetScene() {
    scene.remove.apply(scene, scene.children);
    createScene();
    assignCamera("Per");

    if (isPaused) {
        let elem = document.querySelector('#elem');
        document.body.removeChild(elem);
        isPaused = false;
    }
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
        case "1": // perspective fixed camera on scene
            assignCamera("Per");
            break;
        case "2": // orthogonal fixed camera aligned with stand
            assignCamera("Ort");
            break;
        case "3": // restart scene
            resetScene();
            break;

        case "a": // toggle shading
            switchMaterial(floor);

            stand.children.forEach(function (step) {
                switchMaterial(step);
            })

            switchMaterial(initialState);
            switchMaterial(interState);
            switchMaterial(swan);
            break;
        case "s": // toggle lighting calculation (standard material)
            switchMaterial(floor, true);

            stand.children.forEach(function (step) {
                switchMaterial(step, true);
            })

            switchMaterial(initialState, true);
            switchMaterial(interState, true);
            switchMaterial(swan, true);
            break;

        case " ": // pause
            if(!isPaused){
                isPaused = true;
                let text = document.createElement('div');
                text.id = "elem"
                text.style.position = 'absolute';
                text.style.width = 100;
                text.style.height = 100;
                text.style.backgroundColor = "white";
                text.innerHTML = "Paused";
                text.style.top = window.innerHeight/2 + 'px';
                text.style.left = window.innerWidth/2 + 'px';
                document.body.appendChild(text);
            } else {
                isPaused = false;
                let elem = document.querySelector('#elem');
                document.body.removeChild(elem);
            }
            break;

        case "d": // toggle direct light
            direct.visible = !direct.visible;
            break;

        case "z": // toggle spotlight for initial piece
            spot1.children[2].visible = !spot1.children[2].visible;
            break;
        case "x": // toggle spotlight for intermediate piece
            spot2.children[2].visible = !spot2.children[2].visible;
            break;
        case "c": // toggle spotlight for swan (final piece)
            spot3.children[2].visible = !spot3.children[2].visible;
            break;

        default: // movement keys
            if (action_keys.includes(e.key) && !isPaused) {
                pressed_keys.add(e.key);
            }
    }
}

function onKeyUp(e){
    'use strict';
    if (!action_keys.includes(e.key)) return;
    pressed_keys.delete(e.key);

    // turn on rotation of origami pieces
    initialState.userData.onRotation = true;
    interState.userData.onRotation = true;
    swan.userData.onRotation = true;
}

function applyKeyMovement(key, delta=0) {
    switch (key) {
        case "q": // rotate part 1 to left
            initialState.rotateY(-initialState.userData.rotateSpeed * delta);
            initialState.userData.onRotation = false;
            break;
        case "w": // rotate part 1 to right
            initialState.rotateY(initialState.userData.rotateSpeed * delta);
            initialState.userData.onRotation = false;
            break;

        case "e": // rotate part 2 to left
            interState.rotateY(-interState.userData.rotateSpeed * delta);
            interState.userData.onRotation = false;
            break;
        case "r": // rotate part 2 to right
            interState.rotateY(interState.userData.rotateSpeed * delta);
            interState.userData.onRotation = false;
            break;

        case "t": // rotate part 3 to left
            swan.rotateY(-swan.userData.rotateSpeed * delta);
            swan.userData.onRotation = false;
            break;
        case "y": // rotate part 3 to right
            swan.rotateY(swan.userData.rotateSpeed * delta);
            swan.userData.onRotation = false;
            break;
    }
}

function render() {
    'use strict';
    renderer.render(scene, currentCamera);
}

function init() {
    'use strict';

    renderer = new THREE.WebGLRenderer( { antialias: true } );
    renderer.setPixelRatio( window.devicePixelRatio );
    renderer.setSize( window.innerWidth, window.innerHeight );
    renderer.setClearColor( 0x000000, 1 );
    renderer.shadowMap.enabled = true;
    renderer.xr.enabled = true;
    renderer.setAnimationLoop(animate);
    document.body.appendChild(renderer.domElement);

    createScene();

    let aspect = window.innerWidth / window.innerHeight;
    perCamera = createCamera(50, 60, 50, 'Perspective', null, 60, aspect, 1, 200);
    ortCamera = createCamera(stand.position.x, stand.position.y + 25, stand.position.z + 10,
        'Orthographic', new THREE.Vector3(0, 0, -1), -30*aspect, 30*aspect, 30, -30, -18, 30);
    vrCamera = new THREE.StereoCamera();
    vrCamera.cameraL = createCamera(0, 0, 0, 'Perspective', null, 60, aspect, 1, 200);
    vrCamera.cameraR = createCamera(0, 0, 0, 'Perspective', null, 60, aspect, 1, 200);
    renderer.xr.getCamera().cameras.push(vrCamera);
    document.body.appendChild(VRButton.createButton(renderer));

    assignCamera("Per");

    render();

    window.addEventListener("keydown", onKeyDown);
    window.addEventListener("keyup", onKeyUp);
    window.addEventListener("resize", onResize);
}

function autoRotationOrigamiPiece(piece, delta) {
    if (!piece.userData.onRotation) return;

    piece.rotateY(piece.userData.rotateSpeed/18 * delta);
}

function animate() {
    'use strict';

    let delta = Date.now() - timer;
    if (!isPaused) {

        pressed_keys.forEach(function (key) {applyKeyMovement(key, delta);});

        // automatic rotation of origami pieces
        autoRotationOrigamiPiece(initialState, delta);
        autoRotationOrigamiPiece(interState, delta);
        autoRotationOrigamiPiece(swan, delta);
    }
    timer += delta;  // real timer increment

    render();
}