"""Single source for the editable Blockbench model, Java mesh and pixel assets for Void Gargoyle.

Run with Python + Pillow + NumPy. Java model units: +Y down, -Z forward.
Blockbench uses Y=24-javaY; its root pivot is (0, 24, 0).
"""
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont
import base64
import io
import json
import math
import uuid
import numpy as np

ROOT = Path(__file__).resolve().parents[1]
TEX = ROOT / 'src/client/resources/assets/modid/textures'
JAVA = ROOT / 'src/client/java/com/example/client/model/aura/VoidGargoyleModel.java'

# Obsidian stone, lava veins, demonic glowing eyes, and sharp ivory fangs
PALETTE = {
    'obsidian': '#15131c',
    'obsidian_dark': '#0c0a10',
    'obsidian_mid': '#1d1927',
    'obsidian_edge': '#2c2838',
    'obsidian_light': '#3d374d',
    'lava_dark': '#b31500',
    'lava_red': '#ff2200',
    'lava_mid': '#ff5500',
    'lava_hot': '#ff9900',
    'lava_core': '#ffdd33',
    'eye_red': '#ff1111',
    'eye_pupil': '#ffee55',
    'fang_white': '#eaeaf0',
    'fang_shade': '#b2b2be',
    'throat_fire': '#ff2200',
}

# 8x8 swatch tiles for detailed accents
SWATCH = {
    'chest_core': (0, 32),
    'eye': (8, 32),
    'pupil': (16, 32),
    'fang': (24, 32),
    'throat': (32, 32),
    'brow': (40, 32),
    'wing_hook': (48, 32),
    'obsidian_swatch': (56, 32),
}

# Explicit Box UV offsets for major structural meshes:
BOX_UV = {
    'torso': (0, 0),
    'skull': (20, 0),
    'jaw_bone': (0, 10),
    'claw_l': (42, 0),
    'claw_r': (53, 0),
    'wing_l_membrane': (0, 18),
    'wing_r_membrane': (14, 18),
    'wing_l_arm': (28, 18),
    'wing_r_arm': (28, 21),
    'wing_l_strut': (40, 18),
    'wing_r_strut': (46, 18),
    'horn_l_base': (0, 26),
    'horn_l_tip': (9, 26),
    'horn_r_base': (15, 26),
    'horn_r_tip': (24, 26),
    'tail_base': (30, 26),
    'tail_mid': (40, 26),
    'tail_spade': (48, 26),
    'tail_point': (57, 26),
}

# Joints: (origin(ox, oy, oz), initial_rotation(rx, ry, rz))
JOINTS = {
    'root': ((0.0, 0.0, 0.0), (0.0, 0.0, 0.0)),
    'body': ((0.0, 0.0, 0.0), (0.0, 0.0, 0.0)),
    'head': ((0.0, -1.0, -1.0), (0.0, 0.0, 0.0)),
    'lower_jaw': ((0.0, -0.3, -1.0), (0.0, 0.0, 0.0)),
    'wing_l': ((-2.2, -0.5, 1.4), (0.0, 0.20, 0.10)),
    'wing_r': ((2.2, -0.5, 1.4), (0.0, -0.20, -0.10)),
    'tail': ((0.0, 2.6, 2.2), (0.15, 0.0, 0.0)),
}

# Parts: name, (x, y, z), (w, h, d), parent_joint, color/swatch_tag
PARTS = [
    # --- Body ---
    ('torso', (-2.5, -1.0, -1.5), (5.0, 5.0, 4.0), 'body', 'obsidian'),
    ('chest_core', (-1.5, 0.2, -1.6), (3.0, 2.6, 0.2), 'body', 'chest_core'),
    ('claw_l', (-2.3, 3.6, -2.2), (1.8, 1.2, 2.2), 'body', 'claw_l'),
    ('claw_r', (0.5, 3.6, -2.2), (1.8, 1.2, 2.2), 'body', 'claw_r'),

    # --- Head ---
    ('skull', (-2.5, -2.5, -3.2), (5.0, 3.2, 4.2), 'head', 'obsidian'),
    ('brow', (-2.5, -2.2, -3.4), (5.0, 0.6, 0.8), 'head', 'brow'),
    ('eye_l', (-2.1, -1.7, -3.32), (1.4, 0.8, 0.15), 'head', 'eye'),
    ('eye_r', (0.7, -1.7, -3.32), (1.4, 0.8, 0.15), 'head', 'eye'),
    ('pupil_l', (-1.6, -1.55, -3.36), (0.5, 0.5, 0.1), 'head', 'pupil'),
    ('pupil_r', (1.1, -1.55, -3.36), (0.5, 0.5, 0.1), 'head', 'pupil'),
    ('upper_fang_l', (-1.9, 0.7, -3.0), (0.7, 0.8, 0.7), 'head', 'fang'),
    ('upper_fang_r', (1.2, 0.7, -3.0), (0.7, 0.8, 0.7), 'head', 'fang'),
    ('throat_roof', (-1.8, 0.65, -2.6), (3.6, 0.15, 2.4), 'head', 'throat'),
    ('horn_l_base', (-2.3, -3.8, -1.8), (1.2, 1.6, 1.4), 'head', 'horn_l_base'),
    ('horn_l_tip', (-2.1, -5.2, -0.8), (0.8, 1.6, 1.0), 'head', 'horn_l_tip'),
    ('horn_r_base', (1.1, -3.8, -1.8), (1.2, 1.6, 1.4), 'head', 'horn_r_base'),
    ('horn_r_tip', (1.3, -5.2, -0.8), (0.8, 1.6, 1.0), 'head', 'horn_r_tip'),

    # --- Lower Jaw (Pivot at back-bottom of head: Y=-0.3, Z=-1.0) ---
    ('jaw_bone', (-2.2, 0.0, -3.2), (4.4, 1.4, 3.2), 'lower_jaw', 'obsidian'),
    ('lower_fang_l', (-1.7, -0.7, -2.8), (0.6, 0.7, 0.6), 'lower_jaw', 'fang'),
    ('lower_fang_r', (1.1, -0.7, -2.8), (0.6, 0.7, 0.6), 'lower_jaw', 'fang'),
    ('throat_floor', (-1.7, 0.01, -2.6), (3.4, 0.15, 2.4), 'lower_jaw', 'throat'),

    # --- Left Wing ---
    ('wing_l_arm', (-3.6, -0.6, -0.4), (3.8, 0.8, 0.8), 'wing_l', 'wing_l_arm'),
    ('wing_l_hook', (-4.4, -1.8, -0.3), (1.0, 1.4, 0.6), 'wing_l', 'wing_hook'),
    ('wing_l_strut', (-5.2, -0.6, -0.3), (1.0, 3.2, 0.6), 'wing_l', 'wing_l_strut'),
    ('wing_l_membrane', (-4.8, 0.0, 0.0), (4.8, 4.4, 0.1), 'wing_l', 'wing_l_membrane'),

    # --- Right Wing ---
    ('wing_r_arm', (-0.2, -0.6, -0.4), (3.8, 0.8, 0.8), 'wing_r', 'wing_r_arm'),
    ('wing_r_hook', (3.4, -1.8, -0.3), (1.0, 1.4, 0.6), 'wing_r', 'wing_hook'),
    ('wing_r_strut', (4.2, -0.6, -0.3), (1.0, 3.2, 0.6), 'wing_r', 'wing_r_strut'),
    ('wing_r_membrane', (0.0, 0.0, 0.0), (4.8, 4.4, 0.1), 'wing_r', 'wing_r_membrane'),

    # --- Tail ---
    ('tail_base', (-0.6, -0.6, 0.0), (1.2, 1.2, 2.0), 'tail', 'tail_base'),
    ('tail_mid', (-0.5, -1.2, 1.8), (1.0, 1.0, 2.0), 'tail', 'tail_mid'),
    ('tail_spade', (-1.4, -2.0, 3.6), (2.8, 1.8, 0.2), 'tail', 'tail_spade'),
    ('tail_point', (-0.4, -2.4, 3.5), (0.8, 2.4, 0.4), 'tail', 'tail_point'),
]


def uid(name):
    return str(uuid.uuid5(uuid.NAMESPACE_URL, 'kiem:void_gargoyle:' + name))


def uv_for(part):
    name = part[0]
    color_tag = part[4]
    if name in BOX_UV:
        return BOX_UV[name]
    if color_tag in SWATCH:
        return SWATCH[color_tag]
    return (0, 0)


def box_faces(x, y, w, h, d):
    iw, ih, id_ = int(math.ceil(w)), int(math.ceil(h)), int(math.ceil(d))
    return {
        'north': [x + id_, y + id_, x + id_ + iw, y + id_ + ih],
        'east': [x, y + id_, x + id_, y + id_ + ih],
        'south': [x + id_ + iw + id_, y + id_, x + id_ + iw + id_ + iw, y + id_ + ih],
        'west': [x + id_ + iw, y + id_, x + id_ + iw + id_, y + id_ + ih],
        'up': [x + id_ + iw, y + id_, x + id_, y],
        'down': [x + id_ + iw + iw, y, x + id_ + iw, y + id_],
    }


def draw_box_net(dr, u, v, w, h, d, base_col, edge_col=None, crack_col=None):
    iw, ih, id_ = int(math.ceil(w)), int(math.ceil(h)), int(math.ceil(d))
    faces = box_faces(u, v, iw, ih, id_)
    for face, (x1, y1, x2, y2) in faces.items():
        xa, xb = sorted((x1, x2))
        ya, yb = sorted((y1, y2))
        dr.rectangle((xa, ya, xb - 1, yb - 1), fill=base_col)
        if edge_col:
            dr.rectangle((xa, ya, xb - 1, yb - 1), outline=edge_col)


def texture():
    im = Image.new('RGBA', (64, 64), (0, 0, 0, 0))
    dr = ImageDraw.Draw(im)

    # 1. Fill base dark obsidian background for safety
    dr.rectangle((0, 0, 63, 63), fill=(15, 13, 20, 255))

    # 2. Torso (u=0, v=0, w=5, h=5, d=4)
    draw_box_net(dr, 0, 0, 5, 5, 4, '#15131c', '#2c2838')
    # Molten lava vein on chest (north face: x=4..8, y=4..8)
    dr.line([(6, 4), (6, 6), (7, 8)], fill='#ff3b14', width=1)
    dr.point([(6, 5)], fill='#ffaa00')

    # 3. Skull (u=20, v=0, w=5, h=4, d=5)
    draw_box_net(dr, 20, 0, 5, 4, 5, '#15131c', '#2c2838')
    # Molten nostrils / face cracks (north face: x=25..29, y=5..8)
    dr.point([(26, 7), (28, 7)], fill='#ff2200')
    dr.point([(27, 6)], fill='#ff5500')
    # Crown cracks on top (up face: x=25..29, y=0..4)
    dr.line([(26, 1), (27, 2), (28, 1)], fill='#ff3b14', width=1)

    # 4. Jaw Bone (u=0, v=10, w=5, h=2, d=4)
    draw_box_net(dr, 0, 10, 5, 2, 4, '#15131c', '#2c2838')
    # Inside floor of lower jaw (up face: x=4..8, y=10..13) -> glowing molten throat bed!
    dr.rectangle((4, 10, 8, 13), fill='#ff2200')
    dr.point([(6, 11), (6, 12)], fill='#ff8800')

    # 5. Claws (claw_l: 42, 0; claw_r: 53, 0)
    draw_box_net(dr, 42, 0, 2, 2, 3, '#15131c', '#3d374d')
    dr.point([(45, 4), (46, 4)], fill='#eaeaf0')
    draw_box_net(dr, 53, 0, 2, 2, 3, '#15131c', '#3d374d')
    dr.point([(56, 4), (57, 4)], fill='#eaeaf0')

    # 6. Wing membranes (wing_l: 0, 18; wing_r: 14, 18)
    draw_box_net(dr, 0, 18, 5, 5, 1, '#1b1724', '#2c2838')
    # Magma veins branching in left wing
    dr.line([(2, 19), (4, 21), (5, 23)], fill='#ff3b14', width=1)
    dr.line([(4, 21), (3, 23)], fill='#ff7700', width=1)
    draw_box_net(dr, 14, 18, 5, 5, 1, '#1b1724', '#2c2838')
    # Magma veins in right wing
    dr.line([(20, 19), (18, 21), (17, 23)], fill='#ff3b14', width=1)
    dr.line([(18, 21), (19, 23)], fill='#ff7700', width=1)

    # 7. Wing arms & struts
    draw_box_net(dr, 28, 18, 4, 1, 1, '#15131c', '#2c2838')
    draw_box_net(dr, 28, 21, 4, 1, 1, '#15131c', '#2c2838')
    draw_box_net(dr, 40, 18, 1, 4, 1, '#15131c', '#2c2838')
    draw_box_net(dr, 46, 18, 1, 4, 1, '#15131c', '#2c2838')

    # 8. Horns
    draw_box_net(dr, 0, 26, 2, 2, 2, '#15131c', '#2c2838')
    dr.point([(2, 28), (3, 28)], fill='#ff3b14')
    draw_box_net(dr, 9, 26, 1, 2, 1, '#1d1927', '#ff3b14')
    draw_box_net(dr, 15, 26, 2, 2, 2, '#15131c', '#2c2838')
    dr.point([(17, 28), (18, 28)], fill='#ff3b14')
    draw_box_net(dr, 24, 26, 1, 2, 1, '#1d1927', '#ff3b14')

    # 9. Tail
    draw_box_net(dr, 30, 26, 2, 2, 2, '#15131c', '#2c2838')
    dr.point([(32, 28), (33, 28)], fill='#ff3b14')
    draw_box_net(dr, 40, 26, 1, 1, 2, '#15131c', '#2c2838')
    draw_box_net(dr, 48, 26, 3, 2, 1, '#15131c', '#ff2200')
    draw_box_net(dr, 57, 26, 1, 3, 1, '#ff2200', '#ff9900')

    # 10. Accent Swatches (Y = 32..39)
    # Chest core: blazing demonic heart
    dr.rectangle((0, 32, 7, 39), fill='#b31500')
    dr.rectangle((1, 33, 6, 38), fill='#ff2200')
    dr.rectangle((2, 34, 5, 37), fill='#ff6600')
    dr.rectangle((3, 35, 4, 36), fill='#ffdd33')

    # Eye: fiery ruby gradient
    dr.rectangle((8, 32, 15, 39), fill='#ff1111')
    dr.line([(9, 35), (14, 35)], fill='#ff7700')
    dr.point([(11, 35), (12, 35)], fill='#ffcc00')

    # Pupil: glowing yellow-hot core
    dr.rectangle((16, 32, 23, 39), fill='#ffee44')
    dr.rectangle((18, 34, 21, 37), fill='#ffffff')

    # Fang: sharp ivory tooth with subtle shade
    dr.rectangle((24, 32, 31, 39), fill='#eaeaf0')
    dr.line([(24, 39), (31, 39)], fill='#b0b0be')
    dr.point([(27, 32), (28, 32)], fill='#ffffff')

    # Throat: roaring molten interior
    dr.rectangle((32, 32, 39, 39), fill='#ff1100')
    dr.rectangle((33, 33, 38, 38), fill='#ff3b14')
    dr.rectangle((34, 34, 37, 37), fill='#ff8800')
    dr.rectangle((35, 35, 36, 36), fill='#fff2a8')

    # Brow & wing hooks: sharp obsidian stone ridges
    dr.rectangle((40, 32, 47, 39), fill='#2c2838')
    dr.rectangle((41, 33, 46, 38), fill='#3d374d')
    dr.rectangle((48, 32, 55, 39), fill='#201c2a')
    dr.line([(48, 32), (55, 39)], fill='#4d4560')
    dr.rectangle((56, 32, 63, 39), fill='#15131c')

    return im


def java_model():
    def n(f):
        return f'{float(f):.4f}f'

    lines = [
        'package com.example.client.model.aura;',
        '',
        'import net.minecraft.client.model.Model;',
        'import net.minecraft.client.model.geom.ModelPart;',
        'import net.minecraft.client.model.geom.PartPose;',
        'import net.minecraft.client.model.geom.builders.*;',
        'import net.minecraft.client.renderer.rendertype.RenderTypes;',
        '',
        '/** Generated by tools/generate_gargoyle_assets.py; edit its shared geometry. */',
        'public final class VoidGargoyleModel extends Model<Float> {',
        '    public final ModelPart body;',
        '    public final ModelPart head;',
        '    public final ModelPart lowerJaw;',
        '    public final ModelPart leftWing;',
        '    public final ModelPart rightWing;',
        '    public final ModelPart tail;',
        '',
        '    public VoidGargoyleModel() {',
        '        super(createLayer().bakeRoot(), RenderTypes::entityCutout);',
        '        this.body = this.root.getChild("body");',
        '        this.head = this.root.getChild("head");',
        '        this.lowerJaw = this.root.getChild("lower_jaw");',
        '        this.leftWing = this.root.getChild("wing_l");',
        '        this.rightWing = this.root.getChild("wing_r");',
        '        this.tail = this.root.getChild("tail");',
        '    }',
        '',
        '    public static LayerDefinition createLayer() {',
        '        MeshDefinition mesh = new MeshDefinition();',
        '        PartDefinition root = mesh.getRoot();',
    ]

    for name, (origin, rot) in JOINTS.items():
        if name == 'root':
            continue
        vals = ', '.join(map(n, (*origin, *rot)))
        lines.append(f'        PartDefinition {name} = root.addOrReplaceChild("{name}", CubeListBuilder.create(), PartPose.offsetAndRotation({vals}));')

    for part in PARTS:
        name, pos, size, parent, col = part
        u, v = uv_for(part)
        lines.append(f'        {parent}.addOrReplaceChild("{name}", CubeListBuilder.create().texOffs({u}, {v}).addBox({", ".join(map(n, (*pos, *size)))}), PartPose.ZERO);')

    lines += [
        '        return LayerDefinition.create(mesh, 64, 64);',
        '    }',
        '',
        '    @Override',
        '    public void setupAnim(Float age) {',
        '        resetPose();',
        '        float t = age != null ? age : 0.0f;',
        '        // Smooth bat wing flap cycle',
        '        float wingFlap = (float) Math.sin(t * 0.22f) * 0.38f;',
        '        // Snarl breathing jaw cycle',
        '        float jawCycle = (float) Math.sin(t * 0.08f);',
        '        float idleJaw = jawCycle > 0.65f ? (jawCycle - 0.65f) * 0.45f : 0.0f;',
        '        setJawAndWings(idleJaw, wingFlap);',
        '        // Demonic tail swish',
        '        tail.yRot += (float) Math.sin(t * 0.16f) * 0.25f;',
        '        tail.xRot += (float) Math.cos(t * 0.16f) * 0.10f;',
        '    }',
        '',
        '    public void setJawAndWings(float jawAngle, float wingFlap) {',
        '        lowerJaw.xRot = jawAngle;',
        '        leftWing.zRot = wingFlap;',
        '        rightWing.zRot = -wingFlap;',
        '        leftWing.yRot = wingFlap * 0.35f;',
        '        rightWing.yRot = -wingFlap * 0.35f;',
        '    }',
        '}',
        '',
    ]
    return '\n'.join(lines)


def blockbench(tex):
    elements = []
    for part in PARTS:
        name, (x, y, z), (w, h, d), parent, col = part
        ox, oy, oz = JOINTS[parent][0]
        u, v = uv_for(part)
        elements.append({
            'name': name,
            'uuid': uid(name),
            'type': 'cube',
            'from': [x + ox, 24 - (y + oy + h), z + oz],
            'to': [x + ox + w, 24 - (y + oy), z + oz + d],
            'origin': [ox, 24 - oy, oz],
            'box_uv': True,
            'uv_offset': [u, v],
            'faces': {key: {'uv': value, 'texture': 0} for key, value in box_faces(u, v, w, h, d).items()}
        })

    children = [uid(p[0]) for p in PARTS if p[3] == 'root']
    for name, (origin, rot) in JOINTS.items():
        if name == 'root':
            continue
        ox, oy, oz = origin
        rx, ry, rz = rot
        children.append({
            'name': name,
            'uuid': uid(name),
            'origin': [ox, 24 - oy, oz],
            'rotation': [-math.degrees(rx), -math.degrees(ry), math.degrees(rz)],
            'export': True,
            'children': [uid(p[0]) for p in PARTS if p[3] == name]
        })

    buf = io.BytesIO()
    tex.save(buf, format='PNG')
    return {
        'meta': {'format_version': '5.0', 'model_format': 'modded_entity', 'box_uv': True},
        'name': 'Void Gargoyle',
        'model_identifier': 'void_gargoyle',
        'modded_entity_version': '1.17',
        'modded_entity_flip_y': True,
        'resolution': {'width': 64, 'height': 64},
        'elements': elements,
        'outliner': [{
            'name': 'root',
            'uuid': uid('root'),
            'origin': [0, 24, 0],
            'rotation': [0, 0, 0],
            'export': True,
            'children': children
        }],
        'textures': [{
            'name': 'void_gargoyle.png',
            'id': '0',
            'uuid': uid('texture'),
            'width': 64,
            'height': 64,
            'uv_width': 64,
            'uv_height': 64,
            'mode': 'bitmap',
            'source': 'data:image/png;base64,' + base64.b64encode(buf.getvalue()).decode()
        }]
    }


def preview():
    s = 3
    im = Image.new('RGBA', (256 * s, 192 * s), '#0d0b14')
    dr = ImageDraw.Draw(im)
    # Volcanic pedestal / shadow
    dr.ellipse((35 * s, 145 * s, 220 * s, 178 * s), fill='#230c14')

    yaw = math.radians(-26)
    pitch = math.radians(16)

    def transform(v, parent):
        x, y, z = v
        (ox, oy, oz), (rx, ry, rz) = JOINTS[parent]
        # Apply joint rotation
        # Simple pitch & roll for preview
        if rx != 0:
            y, z = y * math.cos(rx) - z * math.sin(rx), y * math.sin(rx) + z * math.cos(rx)
        if ry != 0:
            x, z = x * math.cos(ry) + z * math.sin(ry), -x * math.sin(ry) + z * math.cos(ry)
        if rz != 0:
            x, y = x * math.cos(rz) - y * math.sin(rz), x * math.sin(rz) + y * math.cos(rz)

        x, y, z = x + ox, y + oy, z + oz
        xx = x * math.cos(yaw) + z * math.sin(yaw)
        zz = -x * math.sin(yaw) + z * math.cos(yaw)
        yy = y * math.cos(pitch) - zz * math.sin(pitch)
        zz = y * math.sin(pitch) + zz * math.cos(pitch)
        return (128 * s + xx * 22 * s, 98 * s + yy * 22 * s, zz)

    pixels = np.asarray(im).copy()
    depth = np.full((im.height, im.width), np.inf)

    # Color mapping for preview
    def get_color(col):
        if col in PALETTE:
            hex_c = PALETTE[col]
        elif col in SWATCH:
            hex_c = {'chest_core': '#ff3b14', 'eye': '#ff1111', 'pupil': '#ffee55',
                     'fang': '#eaeaf0', 'throat': '#ff2200', 'brow': '#2c2838',
                     'wing_hook': '#3d374d', 'obsidian_swatch': '#15131c'}.get(col, '#15131c')
        else:
            hex_c = '#15131c'
        return tuple(int(hex_c[i:i+2], 16) for i in (1, 3, 5))

    for part in PARTS:
        name, (x, y, z), (w, h, dep), parent, col = part
        verts = [transform((x + a * w, y + b * h, z + c * dep), parent) for a, b, c in
                 [(0,0,0),(1,0,0),(1,1,0),(0,1,0),(0,0,1),(1,0,1),(1,1,1),(0,1,1)]]
        base_rgb = get_color(col)

        for ix, shade in [([0,1,2,3], 1.0), ([4,0,3,7], 0.82), ([0,4,5,1], 1.15),
                          ([1,5,6,2], 0.88), ([5,4,7,6], 0.78), ([3,2,6,7], 0.75)]:
            rgb = tuple(min(255, int(v * shade)) for v in base_rgb)
            for ids in [(ix[0], ix[1], ix[2]), (ix[0], ix[2], ix[3])]:
                a, b, c = (verts[k] for k in ids)
                xmin = max(0, math.floor(min(a[0], b[0], c[0])))
                xmax = min(im.width - 1, math.ceil(max(a[0], b[0], c[0])))
                ymin = max(0, math.floor(min(a[1], b[1], c[1])))
                ymax = min(im.height - 1, math.ceil(max(a[1], b[1], c[1])))
                if xmin > xmax or ymin > ymax:
                    continue
                xx, yy = np.meshgrid(np.arange(xmin, xmax + 1) + 0.5, np.arange(ymin, ymax + 1) + 0.5)
                den = (b[1] - c[1]) * (a[0] - c[0]) + (c[0] - b[0]) * (a[1] - c[1])
                if abs(den) < 1e-8:
                    continue
                u = ((b[1] - c[1]) * (xx - c[0]) + (c[0] - b[0]) * (yy - c[1])) / den
                v = ((c[1] - a[1]) * (xx - c[0]) + (a[0] - c[0]) * (yy - c[1])) / den
                t = 1 - u - v
                zz = u * a[2] + v * b[2] + t * c[2]
                region = depth[ymin:ymax + 1, xmin:xmax + 1]
                mask = (u >= 0) & (v >= 0) & (t >= 0) & (zz < region)
                region[mask] = zz[mask]
                pixels[ymin:ymax + 1, xmin:xmax + 1][mask] = (*rgb, 255)

    im = Image.fromarray(pixels)
    return im.resize((256, 192), Image.Resampling.LANCZOS)


if __name__ == '__main__':
    (TEX / 'aura').mkdir(parents=True, exist_ok=True)
    (TEX / 'gui').mkdir(parents=True, exist_ok=True)
    JAVA.parent.mkdir(parents=True, exist_ok=True)

    tex = texture()
    tex.save(TEX / 'aura/void_gargoyle.png')

    JAVA.write_text(java_model(), encoding='utf-8')
    (ROOT / 'void_gargoyle.bbmodel').write_text(json.dumps(blockbench(tex), indent=2), encoding='utf-8')
    preview().save(TEX / 'gui/preview_aura_gargoyle.png')

    print(f'Generated Void Gargoyle: {len(PARTS)} parts, articulated lower jaw, flapping wings, atlas, Java model and Blockbench .bbmodel.')
