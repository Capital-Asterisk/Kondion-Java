/*
 * Copyright 2015 Neal Nicdao
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package vendalenger.kondion.scene;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glTexCoordPointer;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joml.Matrix3f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import vendalenger.kondion.KInput;
import vendalenger.kondion.collision.FixedCylinderCollider;
import vendalenger.kondion.lwjgl.TTT;
import vendalenger.kondion.lwjgl.resource.KondionLoader;
import vendalenger.kondion.objects.PhysicEntity;

public class Scene {

	public static final byte AA_A = 0, WEDGE_A = 1, WEDGE_B = 2;

	private List<MapCollider> colliders;

	private FloatBuffer vertData;
	private FloatBuffer cordData;
	private FloatBuffer normData;

	private int cordHandle;
	private int normHandle;
	private int vertHandle;

	private boolean solidWorld = true;

	private MapCollider temp1;
	private boolean temp0 = false;

	public Scene() {
		colliders = new ArrayList<MapCollider>();
	}

	public void addAABlock(Vector3f pos, boolean cutout, int priority, int up,
			int dn, int no, int ea, int so, int we) {
		MapCollider c = new MapCollider();
		c.shape = AA_A;
		c.solid = !cutout;
		c.up = up;
		c.dn = dn;
		c.no = no;
		c.ea = ea;
		c.so = so;
		c.we = we;
		c.x = pos.x;
		c.y = pos.y;
		c.z = pos.z;
		c.priority = priority;
		colliders.add(c);
	}

	public void doGlBuffers() {

		List<Float> verts = new ArrayList<Float>();
		List<Float> norms = new ArrayList<Float>();
		List<Float> cords = new ArrayList<Float>();

		Vector3f a = new Vector3f();
		Vector3f b = new Vector3f();
		Vector3f c = new Vector3f();
		Vector3f d = new Vector3f();

		Area[] walls = new Area[6];

		List<Vector2f> polys;

		MapCollider to;

		for (int i = 0; i < colliders.size(); i++) {
			to = colliders.get(i);
			if (to.shape == AA_A) {
				// loop through each face

				// Create areas

				// Floor
				walls[0] = new Area(new Rectangle2D.Double(-to.we, -to.no,
						to.we + to.ea, to.no + to.so));

				// Ceiling
				walls[1] = new Area(new Rectangle2D.Double(-to.we, -to.no,
						to.we + to.ea, to.no + to.so));

				// North wall
				walls[2] = new Area(new Rectangle2D.Double(-to.we, -to.dn,
					to.we + to.ea, to.up + to.dn));

				// East wall
				walls[3] = new Area(new Rectangle2D.Double(-to.no, -to.dn,
						to.so + to.no, to.up + to.dn));

				// South wall
				walls[4] = new Area(new Rectangle2D.Double(-to.we, -to.dn,
						to.we + to.ea, to.up + to.dn));

				// West Wall
				walls[5] = new Area(new Rectangle2D.Double(-to.no, -to.dn,
						to.so + to.no, to.up + to.dn));

				// Create walls with normals and no holes

				colliders.get(i).walls = new float[4][];

				// West is -x, north is -z
				// {PointA-X, PointA-Z, PointB-X, PointB-Z,
				// A-Top, A-Bottom, B-Top, B-Bottom,
				// unit normx, unit normy,
				// holex, holey, holewidth, holeheight}

				// North
				colliders.get(i).walls[0] = new float[] {
					to.x - to.we, -to.no, // Point A
					to.x + to.ea, -to.no, // Point B
						
					to.y + to.up, to.y - to.dn, // Top & bottom same
					to.y + to.up, to.y - to.dn, // same here
					0, -1};

				// East
				colliders.get(i).walls[1] = new float[] {
					to.ea, to.z - to.no,
					to.ea, to.z + to.so,
					
					to.y + to.up, to.y - to.dn,
					to.y + to.up, to.y - to.dn,
					1, 0};

				// South
				colliders.get(i).walls[2] = new float[] {
					to.x - to.we, to.so, // Point A
					to.x + to.ea, to.so, // Point B
						
					to.y + to.up, to.y - to.dn, // Top & bottom same
					to.y + to.up, to.y - to.dn, // same here
					0, 1};

				// West
				colliders.get(i).walls[3] = new float[] {
					-to.we, to.z - to.no,
					-to.we, to.z + to.so,
					
					to.y + to.up, to.y - to.dn,
					to.y + to.up, to.y - to.dn,
					-1, 0};

				// loop through other colliders and check for collisions, make wall holes
				List<float[]> NHole = new ArrayList<float[]>();
				List<float[]> EHole = new ArrayList<float[]>();
				List<float[]> SHole = new ArrayList<float[]>();
				List<float[]> WHole = new ArrayList<float[]>();
			
				boolean[] exposedWalls = new boolean[] {false, false, false,
						false, false, false};
				boolean xInt, yInt, zInt;
				MapCollider co = null;

				for (int j = 0; j < colliders.size(); j++) {
					co = colliders.get(j);
					xInt = false;
					yInt = false;
					zInt = false;
					if (co != to) {
						// skip itself to prevent errors
						if (!to.solid) {
							// if this is cutout
							// make walls if it cuts into something or if world
							// is solid

							// Get intersections, all intersections true means
							// touching
							xInt = (to.x + to.ea + co.we >= co.x && to.x
									- to.we - co.ea <= co.x);
							yInt = (to.y + to.up + co.dn >= co.y && to.y
									- to.dn - co.up <= co.y);
							zInt = (to.z + to.so + co.no >= co.z && to.z
									- to.no - co.so <= co.z);

							if (yInt && xInt && zInt) {
								// Get walls inside other colliders then
								// subtract area
								// TODO: and also add holes

								// Floor
								if (co.y - co.dn < to.y + to.up
										&& co.y - co.dn < to.y - to.dn) {
									walls[0].subtract(new Area(
											new Rectangle2D.Double(-co.we
													+ (co.x - to.x), -co.no
													+ (co.z - to.z), co.we
													+ co.ea, co.no + co.so)));
								}

								// Ceiling
								if (co.y + co.up > to.y - to.dn
										&& co.y + co.up > to.y + to.up) {
									walls[1].subtract(new Area(
											new Rectangle2D.Double(-co.we
													+ (co.x - to.x), -co.no
													+ (co.z - to.z), co.we
													+ co.ea, co.no + co.so)));
								}

								// North
								if (co.z - co.no < to.z + to.so
										&& co.z - co.no < to.z - to.no) {
									walls[2].subtract(new Area(
											new Rectangle2D.Double(-co.we
													+ (co.x - to.x), -co.dn
													+ (co.y - to.y), co.ea
													+ co.we, co.up + co.dn)));
									NHole.add(new float[] {-co.we
											+ (co.x - to.x), -co.dn
											+ (co.y - to.y), co.ea
											+ co.we, co.up + co.dn});
									//-to.we
								}

								// East
								if (co.x + co.ea > to.x - to.we
										&& co.x + co.ea > to.x + to.ea) {
									walls[3].subtract(new Area(
											new Rectangle2D.Double(-co.no
													+ (co.z - to.z), -co.dn
													+ (co.y - to.y), co.so
													+ co.no, co.up + co.dn)));
									EHole.add(new float[] {-co.no
											+ (co.z - to.z), -co.dn
											+ (co.y - to.y), co.so
											+ co.no, co.up + co.dn});
								}

								// South
								if (co.z + co.so > to.z - to.no
										&& co.z + co.so > to.z + to.so) {
									walls[4].subtract(new Area(
											new Rectangle2D.Double(-co.we
													+ (co.x - to.x), -co.dn
													+ (co.y - to.y), co.ea
													+ co.we, co.up + co.dn)));
									SHole.add(new float[] {-co.we
											+ (co.x - to.x), -co.dn
											+ (co.y - to.y), co.ea
											+ co.we, co.up + co.dn});
								}

								// West
								if (co.x - co.we < to.x + to.ea
										&& co.x - co.we < to.x - to.we) {
									walls[5].subtract(new Area(
											new Rectangle2D.Double(-co.no
													+ (co.z - to.z), -co.dn
													+ (co.y - to.y), co.so
													+ co.no, co.up + co.dn)));
									WHole.add(new float[] {-co.no
											+ (co.z - to.z), -co.dn
											+ (co.y - to.y), co.so
											+ co.no, co.up + co.dn});
								}
							}

							// System.out.println(xInt + " " + yInt + " " +
							// zInt);
						}
					}
				}
				
				colliders.get(i).rectHoles = new float[4][][];
				colliders.get(i).rectHoles[0] = NHole.toArray(new float[NHole.size()][]);
				colliders.get(i).rectHoles[1] = EHole.toArray(new float[EHole.size()][]);
				colliders.get(i).rectHoles[2] = SHole.toArray(new float[SHole.size()][]);
				colliders.get(i).rectHoles[3] = WHole.toArray(new float[WHole.size()][]);

				for (int j = 0; j < walls.length; j++) {

					/*
					 * Old and inefficient wall system
					 * 
					 * List<List<Double[]>> list = new
					 * ArrayList<List<Double[]>>(); double[] coords = new
					 * double[6];
					 * 
					 * System.out.println("NEW WALL" + j);
					 * 
					 * for (PathIterator pi = walls[j].getPathIterator(null);
					 * !pi.isDone(); pi .next()) { pi.currentSegment(coords);
					 * Double[] point = {coords[0], coords[1]};
					 * System.out.println("WALL POINT (" + coords[0] + ", " +
					 * coords[1] + ")"); boolean unique = true;
					 * 
					 * for (int k = 0; k < list.size(); k++) { for (int l = 0; l
					 * < list.get(k).size(); l++) {
					 * 
					 * // if the x is equal to our point's x if
					 * (list.get(k).get(l)[0].equals(point[0])) { unique =
					 * false; boolean duplicate = false;
					 * 
					 * for (int m = 0; m < list.get(k).size(); m++) { //
					 * --RECORD-- THE FARTHEST I'VE GOTTEN IN FOR LOOPS if
					 * (list.get(k).get(m)[1].equals(point[1])) { duplicate =
					 * true; m = list.get(k).size(); } }
					 * 
					 * // Add to that list if no duplicates if (!duplicate)
					 * list.get(k).add(point);
					 * 
					 * // like a break l = list.get(k).size(); } } }
					 * 
					 * if (unique) { list.add(new ArrayList<Double[]>());
					 * list.get(list.size() - 1).add(point); } }
					 * 
					 * System.out.println("PRINTING GROUPS:");
					 * 
					 * for (int k = 0; k < list.size(); k++) {
					 * System.out.println("GROUP: " + k); for (int l = 0; l <
					 * list.get(k).size(); l++) { System.out.println("POINT (" +
					 * list.get(k).get(l)[0] + ", " + list.get(k).get(l)[1] +
					 * ")"); } }
					 */

					try {
						// Triangulate walls into vertex data
						polys = TTT.areaToTriangles(walls[j]);
						for (int k = 0; k < polys.size(); k++) {
							switch (j) {
								case 0:
									a.set(polys.get(k).x, -to.dn,
											polys.get(k).y);
								break;
								case 1:
									a.set(polys.get(k).x, to.up, polys.get(k).y);
								break;
								case 2:
									a.set(polys.get(k).x, polys.get(k).y,
											-to.no);
								break;
								case 3:
									a.set(to.ea, polys.get(k).y, polys.get(k).x);
								break;
								case 4:
									a.set(polys.get(k).x, polys.get(k).y, to.so);
								break;
								case 5:
									a.set(-to.we, polys.get(k).y,
											polys.get(k).x);
								break;
							}
							a.add(to.x, to.y, to.z);
							TTT.addVect(verts, a);
							cords.add(16 / (to.we + to.ea) * polys.get(k).x);
							cords.add(16 / (to.no + to.so) * polys.get(k).y);
						}
					} catch (java.lang.ArrayIndexOutOfBoundsException e) {

					}
				}
			}
		}

		vertData = BufferUtils.createFloatBuffer(verts.size());
		normData = BufferUtils.createFloatBuffer(norms.size());
		cordData = BufferUtils.createFloatBuffer(cords.size());

		// cordData.flip();

		TTT.listPutFloatBuffer(verts, vertData);
		TTT.listPutFloatBuffer(norms, normData);
		TTT.listPutFloatBuffer(cords, cordData);

		vertData.flip();
		normData.flip();
		cordData.flip();

		vertHandle = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vertHandle);
		glBufferData(GL_ARRAY_BUFFER, vertData, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		// normHandle = glGenBuffers();
		// glBindBuffer(GL_ARRAY_BUFFER, normHandle);
		// glBufferData(GL_ARRAY_BUFFER, normData,
		// GL_STATIC_DRAW);
		// glBindBuffer(GL_ARRAY_BUFFER, 0);

		cordHandle = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, cordHandle);
		glBufferData(GL_ARRAY_BUFFER, cordData, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return true if point is not inside a wall
	 */
	public boolean checkPointCollision(float x, float y, float z) {
		temp0 = false;
		for (int i = 0; i < colliders.size(); i++) {
			temp1 = colliders.get(i);

			if ((temp1.x - temp1.we < x && x < temp1.x + temp1.ea)
					&& (temp1.y - temp1.dn < y && y < temp1.y + temp1.up)
					&& (temp1.z - temp1.no < z && z < temp1.z + temp1.so)) {

				if (colliders.get(i).solid) {

					return false;
				} else {

					return true;
				}
			}
		}

		return false;
	}
	
	String[] eggs = new String[] {"North", "East", "South", "West"};
	/**
	 * 
	 * @param e
	 * @param c
	 */
	public void entityCheckFixedCylinder(PhysicEntity e,
			FixedCylinderCollider c) {
		
		c.collisionAmt = 0;
		
		boolean ret = false;
		
		for (int i = 0; i < colliders.size(); i++) {
			temp1 = colliders.get(i);

			// Check if in range
			if ((temp1.y + temp1.up > e.getPosition().y - c.up - c.dn)
					&& (temp1.y - temp1.dn < e.getPosition().y + c.dn + c.up)
					&& (temp1.x - temp1.we < e.getPosition().x && e.getPosition().x < temp1.x + temp1.ea)
					&& (temp1.z - temp1.no < e.getPosition().z && e.getPosition().z < temp1.z + temp1.so)) {
				// In range!
				
				// Now do height collisions
				// Bottom
				if (temp1.y - temp1.dn > e.getPosition().y - c.dn) {
					// You tripped
					c.collisions[c.collisionAmt][0] = 0;
					c.collisions[c.collisionAmt][1] = 1;
					c.collisions[c.collisionAmt][2] = 0;
					c.collisions[c.collisionAmt][3] = (temp1.y - temp1.dn) - (e.getPosition().y - c.dn);
					c.collisionAmt ++;
					//System.out.println("bottom");
				}
				
				// Top
				if (temp1.y + temp1.up < e.getPosition().y + c.up) {
					// You raised the roof
					c.collisions[c.collisionAmt][0] = 0;
					c.collisions[c.collisionAmt][1] = -1;
					c.collisions[c.collisionAmt][2] = 0;
					c.collisions[c.collisionAmt][3] = ((e.getPosition().y + c.up) - (temp1.y + temp1.up));
					c.collisionAmt ++;
				}
				
				for (int j = 0; j < temp1.walls.length; j++) {
					
					// Make a new matrix
					Matrix3f mat = new Matrix3f();
					
					TTT.rotateByUnitVector(mat,
							temp1.walls[j][8], // normal x
							temp1.walls[j][9], // normal y
							0, -1, 0);
					
					
					mat.invert(); // invert it
					
					Vector3f pointA = new Vector3f( // wall point A
							temp1.walls[j][0],
							0,
							temp1.walls[j][1]);
					Vector3f pointB = new Vector3f( // wall point B
							temp1.walls[j][2],
							0,
							temp1.walls[j][3]);
					
					
					Vector3f pos = new Vector3f();
					pos.add(e.getPosition());
					pos.sub(temp1.x, temp1.y, temp1.z);
					mat.transform(pos, pos);
					mat.transform(pointA);
					mat.transform(pointB);
					
					if (pointA.x - pos.x < c.radius) {
						// Collision Detected with Wall!
						
						//System.out.println((float) Math.cos((pointA.x - pos.x) * (Math.PI / c.radius / 2)));
						
						// Too much formal code... needs something informal right now
						// it really motivates me
						boolean fucked = false;
						float val = (float) Math.cos((pointA.x - pos.x) * (Math.PI / c.radius / 2));
						
						for (int k = 0; k < temp1.rectHoles[j].length; k++) {
							// check if in hole
							// temp1.rectHoles[i][k][0] is x
							// temp1.rectHoles[i][k][1] is y
							
							if (temp1.rectHoles[j][k][0] < pos.z + val
								&& temp1.rectHoles[j][k][0] + temp1.rectHoles[j][k][2] > pos.z - val
								&& temp1.rectHoles[j][k][1] < pos.y + c.dn // c.up cancels out: pos.y - c.up + c.up + c.dn
								&& temp1.rectHoles[j][k][1] + temp1.rectHoles[j][k][3] > pos.y - c.up) {
								fucked = true;
							}
						}
						
						if (!fucked) {
							c.collisions[c.collisionAmt][0] = temp1.walls[j][8];
							c.collisions[c.collisionAmt][1] = 0;
							c.collisions[c.collisionAmt][2] = temp1.walls[j][9];
							c.collisions[c.collisionAmt][3] = pointA.x - pos.x - c.radius;
							c.collisionAmt ++;
						}
					}
					
				}
				
			} else {
				
				
			}

		}
		
	}

	public List<MapCollider> getColliders() {
		return colliders;
	}

	public void render() {

		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);

		glBindBuffer(GL_ARRAY_BUFFER, vertHandle);
		glVertexPointer(3, GL_FLOAT, 0, 0l);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		// glBindBuffer(GL_ARRAY_BUFFER, normHandle);
		// glNormalPointer(GL_FLOAT, 0, 0l);
		// glBindBuffer(GL_ARRAY_BUFFER, 0);

		glBindBuffer(GL_ARRAY_BUFFER, cordHandle);
		glTexCoordPointer(2, GL_FLOAT, 0, 0l);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		KondionLoader.textures.get("noah").bind();
		// glDisable(GL_TEXTURE_2D);
		// glColor3f(0.0f, 1.0f, 1.0f);
		glDrawArrays(GL_TRIANGLES, 0, vertData.capacity() / 3);
		glColor3f(1.0f, 1.0f, 1.0f);

		glDisableClientState(GL_VERTEX_ARRAY);
		// glDisableClientState(GL_NORMAL_ARRAY);
		glDisableClientState(GL_TEXTURE_COORD_ARRAY);
	}
}