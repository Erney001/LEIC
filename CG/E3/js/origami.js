const textureLoader = new THREE.TextureLoader();
const origamiPattern = textureLoader.load("./textures/origami_pattern.jpg")

let floor, stand, initialState, interState, swan;
// Origami properties
const FRONT_COLOR = 0x81e8fd, BACK_COLOR = 0xffffff;
const SPECULAR_COLOR = 0xffffff, SHININESS = 1.6;
const BASIC_MATERIAL = [
    // Front side material
    new THREE.MeshBasicMaterial( { color: FRONT_COLOR, map: origamiPattern } ),
    // Back side material
    new THREE.MeshBasicMaterial( { color: BACK_COLOR, side:THREE.BackSide } )
]
const PHONG_MATERIAL = [
    new THREE.MeshPhongMaterial( // Front side material
        { color: FRONT_COLOR, specular: SPECULAR_COLOR, shininess: SHININESS, map: origamiPattern} ),
    new THREE.MeshPhongMaterial( // Back side material
        { color: BACK_COLOR, specular: SPECULAR_COLOR, shininess: SHININESS, side:THREE.BackSide } )
]
const GOURAUD_MATERIAL = [
    // Front side material
    new THREE.MeshLambertMaterial( { color: FRONT_COLOR, map: origamiPattern } ),
    // Back side material
    new THREE.MeshLambertMaterial( { color: BACK_COLOR, side:THREE.BackSide } )
]

let direct, spot1, spot2, spot3;
// Light properties
const DIRECT_LIGHT_COLOR = 0xffffff, DIRECT_LIGHT_LEVEL = 0.6;
const LIGHT_COLOR = 0xF7D488, LIGHT_LEVEL = 1.5;


function createObject(x, y, z, rot, geometry, material) {
    let object = new THREE.Object3D();

    object.add(new THREE.Mesh(geometry, material));
    object.position.set(x, y, z);
    if (rot) object.rotation.setFromVector3(rot);

    return object;
}

function createSwan(x, y, z){
    let geometry = new THREE.BufferGeometry();

    const vertices = new Float32Array([
        // head-left
        -0.05, 3.5 + 8.0, 2.0,
        0, 3.5 + 8.0 + 1.4, 0,
        0, 3.5 + 8.0, 6.0,

        // head-right
        0.05, 3.5 + 8.0, 2.0,
        0, 3.5 + 8.0 + 1.4, 0,
        0, 3.5 + 8.0, 6.0,

        // neck-left front
        -0.05, 3.5 + 8.0, 2.0,
        0, 3.5, 3.0,
        0, 3.5, 0,

        // neck-right front
        0.05, 3.5 + 8.0, 2.0,
        0, 3.5, 3.0,
        0, 3.5, 0,

        // neck-left back
        0, 3.5, 0,
        0, 3.5 + 8.0 + 1.4, 0,
        -0.05, 3.5 + 8.0, 2.0,

        // neck-right back
        0, 3.5, 0,
        0, 3.5 + 8.0 + 1.4, 0,
        0.05, 3.5 + 8.0, 2.0,
        
        // front left
        0, 3.5, 3.0,
        0, 3.5, 0,
        -0.75, 0, 0,

        // front right
        0.75, 0, 0,
        0, 3.5, 0,
        0, 3.5, 3.0,

        // lateral left front
        0, 3.5, 0,
        -0.75 - 0.2, 0, -7.0,
        -0.75, 0, 0,

        // lateral right front
        0.75, 0, 0,
        0.75 + 0.2, 0, -7.0,
        0, 3.5, 0,

        // lateral left back
        0, 3.5, 0,
        0, 3.5, -7.0,
        -0.75 - 0.2, 0, -7.0,

        // lateral right back
        0.75 + 0.2, 0, -7.0,
        0, 3.5, -7.0,
        0, 3.5, 0,

        // tail left
        0, 3.5, -7.0,
        0, 3.5, -7.0 - 6.9,
        -0.75 - 0.2, 0, -7.0,

        // tail right
        0.75 + 0.2, 0, -7.0,
        0, 3.5, -7.0 - 6.9,
        0, 3.5, -7.0,
    ]);

    const indices = [
        0, 2, 1, // Head-left
        3, 4, 5, // Head-right
        0, 8, 7, // Neck-left-front
        3, 7, 8, // Neck-right-front
        8, 0, 1, // Neck-left-back
        8, 4, 3, // Neck-right-back

        7, 8, 20, // Front-left
        21, 8, 7, // Front-right
        8, 25, 20, // Lateral-left-front
        21, 28, 8, // Lateral-right-front
        8, 31, 25, // Lateral-left-back
        28, 31, 8, // Lateral-right-back
        31, 37, 25, // Tail-left
        28, 37, 31 // Tail-right
    ];

    const uvs = new Float32Array([
        0.15,0, 0.2,0.1, 0,0, // Head-left coords
        0,0.15, 0,0, 0.1,0.2, // Head-right coords
        0.15,0, 0.75,0, 0.7,0.25, // Neck-left-front coords
        0,0.15, 0.25,0.7, 0,0.75, // Neck-right-front coords
        0.7,0.25, 0.2,0.1, 0.15,0, // Neck-left-back coords
        0.25,0.7, 0,0.15, 0.1,0.2, // Neck-right-back coords

        0.75,0, 0.7,0.25, 0.3,0.85, // Front-left coords
        0.85,0.3, 0.7,0.25, 0.75,0, // Front-right coords
        0.7,0.25, 0.75,1, 0.3,0.85, // Lateral-left-front coords
        0.85,0.3, 1,0.75, 0.25,0.7, // Lateral-right-front coords
        0.7,0.25, 0.5,0.5, 0.75,1, // Lateral-left-back coords
        1,0.75, 0.5,0.5, 0.25,0.7, // Lateral-right-back coords
        0.5,0.5, 1,1, 0.75,1, // Tail-left coords
        1,0.75, 1,1, 0.5,0.5 // Tail-right coords
    ]);

    geometry.setAttribute('position', new THREE.BufferAttribute(vertices, 3) );
    geometry.setAttribute('uv', new THREE.BufferAttribute(uvs, 2) );
    geometry.setIndex(indices.concat(indices)); // for two side faces

    geometry.clearGroups();
    geometry.addGroup(0, 42, 0);
    geometry.addGroup(42, 84, 1);

    geometry.computeVertexNormals();

    geometry.normalsNeedUpdate = true;
    swan = createObject(x, y, z, new THREE.Vector3(0, -Math.PI/2, 0), geometry, PHONG_MATERIAL);
    swan.userData = { rotateSpeed: 0.001, onRotation: true,
        index: 0, materials: [PHONG_MATERIAL, GOURAUD_MATERIAL], standard: BASIC_MATERIAL, ilum: true};

    scene.add(swan);
}

function createIntermediatePiece(x, y, z){
    let geometry = new THREE.BufferGeometry();
    let hBody = 16.1, hHead = 5;
    let wl = 3.46, ht = 1.57, hb = 1.29, wb = 3.03;

    const vertices = new Float32Array([
        // upper left
        0, hBody, 0,
        0, hBody + hHead, 0,
        -wl, hBody + ht, 0,

        // upper right
        wl, hBody + ht, 0,
        0, hBody + hHead, 0,
        0, hBody, 0,

        // left top
        -wl, hBody + ht, 0,
        -wb, hBody - hb, 0,
        0, hBody, 0,

        // right top
        wl, hBody + ht, 0,
        0, hBody, 0,
        wb, hBody - hb, 0,

        // bottom left
        0, 0, 0,
        0, hBody, 0,
        -wb, hBody - hb, 0,

        // bottom right
        0, 0, 0,
        wb, hBody - hb, 0,
        0, hBody, 0
    ]);

    const indices = [
        0, 1, 2, // Upper-left
        3, 1, 0, // Upper-right
        2, 7, 0, // Left-top
        3, 0, 11, // Right-top
        15, 0, 7, // Bottom-left
        15, 11, 0 // Bottom-right
    ];

    const uvs = new Float32Array([
        0.7,0.7, 1,1, 1,0.75, // Upper-left coords
        0.75,1, 1,1, 0.7,0.7, // Upper-right coords
        1,0.75, 1.15,0.55, 0.7,0.7, // Left-top coords
        0.75,1, 0.7,0.7, 0.55,1.15, // Right-top coords
        0,0, 0.7,0.7, 1.15,0.55, // Bottom-left coords
        0,0, 0.55,1.15, 0.7,0.7 // Bottom-right coords
    ]);

    geometry.setAttribute('position', new THREE.BufferAttribute(vertices, 3) );
    geometry.setAttribute('uv', new THREE.BufferAttribute(uvs, 2) );
    geometry.setIndex(indices.concat(indices)); // for two side faces

    geometry.clearGroups();
    geometry.addGroup(0, 18, 0);
    geometry.addGroup(18, 30, 1);

    geometry.computeVertexNormals();
    geometry.normalsNeedUpdate = true;
    interState = createObject(x, y, z, null, geometry, PHONG_MATERIAL);
    interState.userData = { rotateSpeed: 0.001, onRotation: true,
        index: 0, materials: [PHONG_MATERIAL, GOURAUD_MATERIAL], standard: BASIC_MATERIAL, ilum: true};

    scene.add(interState);
}

function createInitialPiece(x, y, z){
    let geometry = new THREE.BufferGeometry();
    let side = 21.1;

    const vertices = new Float32Array([
        // right triangle
        0, side, 0,
        0, 0, 0,
        side/2, side/2, 0,

        // left triangle
        0, side, 0,
        -side/2, side/2, 0,
        0, 0, 0
    ]);

    const indices = [
        0, 1, 2, // Right
        0, 4, 1, // Left
    ];

    const uvs = new Float32Array([
        1,1, 0,0, 0,1, // Right coords
        1,1, 1,0, 0,0 // Left coords
    ]);

    geometry.setAttribute('position', new THREE.BufferAttribute(vertices, 3) );
    geometry.setAttribute('uv', new THREE.BufferAttribute(uvs, 2) );
    geometry.setIndex(indices.concat(indices)); // for two side faces

    geometry.clearGroups();
    geometry.addGroup(0, 6, 0);
    geometry.addGroup(6, 12, 1);

    geometry.computeVertexNormals();
    geometry.normalsNeedUpdate = true;
    initialState = createObject(x, y, z, null, geometry, PHONG_MATERIAL);
    initialState.userData = { rotateSpeed: 0.001, onRotation: true,
        index: 0, materials: [PHONG_MATERIAL, GOURAUD_MATERIAL], standard: BASIC_MATERIAL, ilum: true};

    scene.add(initialState);
}

function createStand(x, y, z, w1, h1, d1, w2, h2, d2) {
    stand = new THREE.Group();

    let material1 = new THREE.MeshPhongMaterial({color: 0xE29578, specular: "grey"});
    let material2 = new THREE.MeshLambertMaterial({color: 0xE29578});
    let material5 = new THREE.MeshStandardMaterial(
        { color: 0xE29578,
            side: THREE.DoubleSide} );
    let primaryStep = createObject(0, h1/2, 0, null, new THREE.BoxGeometry(w1, h1, d1), material1);
    primaryStep.userData = { index: 0, materials: [material1, material2], standard: material5, ilum: true};

    let material3 = new THREE.MeshPhongMaterial({color: 0xFFD29D, specular: "grey"});
    let material4 = new THREE.MeshLambertMaterial({color: 0xFFD29D});
    let material6 = new THREE.MeshStandardMaterial(
        { color: 0xFFD29D,
            side: THREE.DoubleSide} );
    let secondaryStep = createObject(0, h2/2, d1/2 + d2/2, null, new THREE.BoxGeometry(w2, h2, d2), material3);
    secondaryStep.userData = { index: 0, materials: [material3, material4], standard: material6, ilum: true};

    stand.add(primaryStep, secondaryStep);

    stand.position.set(x, y, z);
    scene.add(stand);
}

function createFloor(x, y, z, w, h) {
    let material1 = new THREE.MeshPhongMaterial({color: 0x98A886, specular: "grey", side: THREE.DoubleSide});
    let material2 = new THREE.MeshLambertMaterial({color: 0x98A886, side: THREE.DoubleSide});
    let material3 = new THREE.MeshStandardMaterial(
        { color: 0x98A886,
            side: THREE.DoubleSide} );
    floor = createObject(x, y, z, new THREE.Vector3(Math.PI/2, 0, 0), new THREE.PlaneGeometry(w, h), material1);
    floor.userData = { index: 0, materials: [material1, material2], standard: material3, ilum: true};

    scene.add(floor);
}

function createSpotlight(x, y, z, target) {
    let spotlight = new THREE.Group();
    let r = 4, h = 7;
    let light = new THREE.SpotLight(LIGHT_COLOR, LIGHT_LEVEL, 90, 0.3);
    light.castShadow = true;

    spotlight.add(
        createObject(0, 0, 0, null,
            new THREE.ConeGeometry(r, h, 12),
            new THREE.MeshBasicMaterial({ color: 0x000000})),
        createObject(0, -h/2, 0, null,
            new THREE.SphereGeometry(r/2, 12, 12),
            new THREE.MeshBasicMaterial({ color: LIGHT_COLOR}))
    );

    light.castShadow = true;
    light.position.set(0, -h/2, 0);
    light.target = target;

    let targetPos = target.position;
    let alpha = Math.atan(Math.abs(z - targetPos.z) / Math.abs(y - targetPos.y));
    spotlight.rotateX(alpha);

    spotlight.add(light);
    spotlight.position.set(x, y, z);

    return spotlight;
}

function addLight(){
    direct = new THREE.DirectionalLight(DIRECT_LIGHT_COLOR, DIRECT_LIGHT_LEVEL);
    direct.position.set(0, 22, 18);
    direct.target.position = new THREE.Vector3(stand.position);
    direct.castShadow = true;
    scene.add(direct);

    spot1 = createSpotlight(initialState.position.x, 47, 8, initialState);
    spot2 = createSpotlight(interState.position.x, 47, 8, interState);
    spot3 = createSpotlight(swan.position.x, 47, 8, swan);

    scene.add(spot1, spot2, spot3);
}

function switchMaterial(object, stand = false) {
    if (object.userData.ilum && !stand){
        object.userData.index++;
        object.userData.index %= object.userData.materials.length;
        object.children[0].material =
            object.userData.materials[object.userData.index];
        return;
    }

    if (stand){
        object.children[0].material = object.userData.standard;
        object.userData.ilum = !object.userData.ilum;
    }
}
