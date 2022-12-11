package io.github.eisoptrophobia.kubryzltcraft.client.render;

import com.google.common.primitives.Ints;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class RenderUtils {
	
	public static void addSprite(IVertexBuilder builder, MatrixStack matrixStack, float x, float y, float z, float u, float v, int overlay) {
		builder.vertex(matrixStack.last().pose(), x, y, z)
				.color(1.0f, 1.0f, 1.0f, 1.0f)
				.uv(u, v)
				.overlayCoords(overlay)
				.normal(1, 0, 0)
				.endVertex();
				
	}
	
	public static int[] vertexToInts(Vector3f pos, int color, TextureAtlasSprite sprite, float u, float v, int light, int normal) {
		return new int[] {
				Float.floatToRawIntBits(pos.x()),
				Float.floatToRawIntBits(pos.y()),
				Float.floatToRawIntBits(pos.z()),
				color,
				Float.floatToRawIntBits(sprite.getU(u)),
				Float.floatToRawIntBits(sprite.getV(v)),
				light,
				normal
		};
	}
	
	public static int packedNormal(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4) {
		Vector3f vp = v4.copy();
		vp.sub(v2);
		Vector3f vq = v3.copy();
		vq.sub(v1);
		vq.cross(vp);
		float norm = (float) Math.sqrt(vq.dot(vq));
		if (norm < 1.0E-4f) {
			norm = 1.0f;
		}
		norm = 1.0f / norm;
		vq.mul(norm);
		int x = ((byte) (vq.x() * 127)) & 0xff;
		int y = ((byte) (vq.y() * 127)) & 0xff;
		int z = ((byte) (vq.z() * 127)) & 0xff;
		return x | (y << 0x08) | (z << 0x10);
	}
	
	public static int[] vertexData(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4, int color, TextureAtlasSprite sprite, float minU, float maxU, float minV, float maxV, int light, int normal) {
		int[] v1ints = vertexToInts(v1, color, sprite, maxU, maxV, light, normal);
		int[] v2ints = vertexToInts(v2, color, sprite, maxU, minV, light, normal);
		int[] v3ints = vertexToInts(v3, color, sprite, minU, minV, light, normal);
		int[] v4ints = vertexToInts(v4, color, sprite, minU, maxV, light, normal);
		return Ints.concat(v1ints, v2ints, v3ints, v4ints);
	}
	
	public static final Vector3f blockWestDownNorth = new Vector3f(0.0f, 0.0f, 0.0f); // 1
	public static final Vector3f blockWestDownSouth = new Vector3f(0.0f, 0.0f, 1.0f);
	public static final Vector3f blockWestUpNorth = new Vector3f(0.0f, 1.0f, 0.0f);  // 2
	public static final Vector3f blockWestUpSouth = new Vector3f(0.0f, 1.0f, 1.0f);
	public static final Vector3f blockEastDownNorth = new Vector3f(1.0f, 0.0f, 0.0f); // 4
	public static final Vector3f blockEastDownSouth = new Vector3f(1.0f, 0.0f, 1.0f);
	public static final Vector3f blockEastUpNorth = new Vector3f(1.0f, 1.0f, 0.0f); // 3
	public static final Vector3f blockEastUpSouth = new Vector3f(1.0f, 1.0f, 1.0f);
	
	private static final Map<Direction, Vector3f[]> orderedBlockVertices = new HashMap<>();
	static {
		orderedBlockVertices.put(Direction.NORTH, new Vector3f[] { blockWestDownNorth, blockWestUpNorth, blockEastUpNorth, blockEastDownNorth });
		orderedBlockVertices.put(Direction.SOUTH, new Vector3f[] { blockEastDownSouth, blockEastUpSouth, blockWestUpSouth, blockWestDownSouth });
		orderedBlockVertices.put(Direction.WEST, new Vector3f[] { blockWestDownSouth, blockWestUpSouth, blockWestUpNorth, blockWestDownNorth });
		orderedBlockVertices.put(Direction.EAST, new Vector3f[] { blockEastDownNorth, blockEastUpNorth, blockEastUpSouth, blockEastDownSouth });
		orderedBlockVertices.put(Direction.DOWN, new Vector3f[] { blockEastDownNorth, blockEastDownSouth, blockWestDownSouth, blockWestDownNorth });
		orderedBlockVertices.put(Direction.UP, new Vector3f[] { blockEastUpSouth, blockEastUpNorth, blockWestUpNorth, blockWestUpSouth });
	}
	
	public static Vector3f[] blockVertices(Direction direction) {
		return orderedBlockVertices.get(direction);
	}
}