package com.technicalitiesmc.base.client;

import com.technicalitiesmc.base.TechnicalitiesKt;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@SideOnly(Side.CLIENT)
public enum EmptyModelLoader implements ICustomModelLoader {

    INSTANCE;

    private final ResourceLocation location = new ResourceLocation(TechnicalitiesKt.MODID, "block/empty");

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
    }

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        return modelLocation.getResourceDomain().equals(TechnicalitiesKt.MODID) && modelLocation.getResourcePath().contains("empty");
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception {
        return new IModel() {

            @Override
            public Collection<ResourceLocation> getTextures() {

                return Collections.emptyList();
            }

            @Override
            public Collection<ResourceLocation> getDependencies() {

                return Collections.emptyList();
            }

            @Override
            public IModelState getDefaultState() {

                return TRSRTransformation.identity();
            }

            @Override
            public IBakedModel bake(IModelState state, VertexFormat format,
                                    Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {

                return new IBakedModel() {

                    @Override
                    public boolean isGui3d() {

                        return false;
                    }

                    @Override
                    public boolean isBuiltInRenderer() {

                        return false;
                    }

                    @Override
                    public boolean isAmbientOcclusion() {

                        return false;
                    }

                    @Override
                    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {

                        return Collections.emptyList();
                    }

                    @Override
                    public TextureAtlasSprite getParticleTexture() {

                        return null;
                    }

                    @Override
                    public ItemOverrideList getOverrides() {

                        return null;
                    }

                    @Override
                    public ItemCameraTransforms getItemCameraTransforms() {

                        return ItemCameraTransforms.DEFAULT;
                    }
                };
            }
        };
    }

}
