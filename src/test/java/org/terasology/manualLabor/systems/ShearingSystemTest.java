// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.manualLabor.systems;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.entity.internal.PojoEntityManager;
import org.terasology.engine.rendering.assets.material.Material;
import org.terasology.engine.rendering.assets.skeletalmesh.SkeletalMesh;
import org.terasology.engine.rendering.logic.SkeletalMeshComponent;
import org.terasology.engine.rendering.opengl.GLSLMaterial;
import org.terasology.engine.rendering.opengl.OpenGLSkeletalMesh;
import org.terasology.gestalt.assets.management.AssetManager;


import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ShearingSystemTest {
    ShearingSystem shearingSystem;
    private EntityRef entity;

    @BeforeEach
    public void setUp() {
        shearingSystem = new ShearingSystem();
        entity = new PojoEntityManager().create();
        SkeletalMeshComponent skeletalMeshComponent = new SkeletalMeshComponent();
        entity.saveComponent(skeletalMeshComponent);
    }

    @Test
    public void testSwitchPrefab() {
        shearingSystem.assetManager = mock(AssetManager.class);
        OpenGLSkeletalMesh mesh = mock(OpenGLSkeletalMesh.class);
        when(shearingSystem.assetManager.getAsset("mesh", SkeletalMesh.class)).thenReturn(Optional.of(mesh));
        GLSLMaterial material = mock(GLSLMaterial.class);
        when(shearingSystem.assetManager.getAsset("material", Material.class)).thenReturn(Optional.of(material));
        shearingSystem.switchPrefab(entity, "mesh", "material");
        SkeletalMeshComponent skeletalMeshComponent = entity.getComponent(SkeletalMeshComponent.class);
        Assertions.assertEquals(skeletalMeshComponent.mesh, mesh);
        Assertions.assertEquals(skeletalMeshComponent.material,material);
    }
}
