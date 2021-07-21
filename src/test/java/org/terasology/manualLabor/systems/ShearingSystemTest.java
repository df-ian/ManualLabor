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
import org.terasology.gestalt.assets.management.AssetManager;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ShearingSystemTest {
    ShearingSystem shearingSystem;
    private EntityRef entity;
    SkeletalMesh initialMesh;
    Material initialMaterial;

    /**
     * Initialize the shearing system and entity to be used while testing. The entity is supposed to mock the animal entity.
     */
    @BeforeEach
    public void setUp() {
        shearingSystem = new ShearingSystem();
        entity = new PojoEntityManager().create();
        SkeletalMeshComponent skeletalMeshComponent = new SkeletalMeshComponent();
        initialMesh = mock(SkeletalMesh.class);
        skeletalMeshComponent.mesh = initialMesh;
        initialMaterial = mock(Material.class);
        skeletalMeshComponent.material = initialMaterial;
        entity.saveComponent(skeletalMeshComponent);
    }

    /**
     * Check whether switchPrefab() changes mesh and material attributes of SkeletalMeshComponent correctly
     */
    @Test
    public void testSwitchPrefabAssetChange() {
        SkeletalMesh expectedMesh = mock(SkeletalMesh.class);
        Material expectedMaterial = mock(Material.class);
        createMockAssetManager(expectedMesh, expectedMaterial);

        SkeletalMeshComponent skeletalMeshComponent = entity.getComponent(SkeletalMeshComponent.class);

        Assertions.assertNotEquals(skeletalMeshComponent.mesh,expectedMesh);
        Assertions.assertNotEquals(skeletalMeshComponent.material,expectedMaterial);

        shearingSystem.switchPrefab(entity, "testMesh", "testMaterial");
        skeletalMeshComponent = entity.getComponent(SkeletalMeshComponent.class);

        Assertions.assertEquals(skeletalMeshComponent.mesh, expectedMesh);
        Assertions.assertEquals(skeletalMeshComponent.material, expectedMaterial);
    }

    /**
     * Check whether switchPrefab() changes mesh and material attributes of SkeletalMeshComponent correctly when mesh is empty. Ideal
     * behaviour in such a case would be to skip the change
     */
    @Test
    public void testSwitchPrefabWithEmptyMaterial() {
        SkeletalMesh expectedMesh = mock(SkeletalMesh.class);
        Material expectedMaterial = mock(Material.class);
        createMockAssetManager(expectedMesh, expectedMaterial);

        shearingSystem.switchPrefab(entity, "testMesh", "empty");
        SkeletalMeshComponent skeletalMeshComponent = entity.getComponent(SkeletalMeshComponent.class);

        Assertions.assertEquals(skeletalMeshComponent.mesh, initialMesh);
        Assertions.assertEquals(skeletalMeshComponent.material, initialMaterial);
    }

    /**
     * Check whether switchPrefab() changes mesh and material attributes of SkeletalMeshComponent correctly when material is empty. Ideal
     * behaviour in such a case would be to skip the change
     */
    @Test
    public void testSwitchPrefabWithEmptyMesh() {
        SkeletalMesh expectedMesh = mock(SkeletalMesh.class);
        Material expectedMaterial = mock(Material.class);
        createMockAssetManager(expectedMesh, expectedMaterial);

        shearingSystem.switchPrefab(entity, "empty", "testMaterial");
        SkeletalMeshComponent skeletalMeshComponent = entity.getComponent(SkeletalMeshComponent.class);

        Assertions.assertEquals(skeletalMeshComponent.mesh, initialMesh);
        Assertions.assertEquals(skeletalMeshComponent.material, initialMaterial);
    }

    private void createMockAssetManager(SkeletalMesh expectedMesh, Material expectedMaterial) {
        shearingSystem.assetManager = mock(AssetManager.class);
        when(shearingSystem.assetManager.getAsset("testMesh", SkeletalMesh.class)).thenReturn(Optional.of(expectedMesh));
        when(shearingSystem.assetManager.getAsset("testMaterial", Material.class)).thenReturn(Optional.of(expectedMaterial));
        when(shearingSystem.assetManager.getAsset("empty", SkeletalMesh.class)).thenReturn(Optional.empty());
        when(shearingSystem.assetManager.getAsset("empty", Material.class)).thenReturn(Optional.empty());
    }

}
