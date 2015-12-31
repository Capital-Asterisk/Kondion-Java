/* KONDION TEST
 * THE KONDION masterscript.js
 * Contains everything about KONDION TEST
 * 
 * 
 * hey paolo.
 */
 
var init = function() {
	KJS.issueCommand("^eggs");
	//^kdion.js (scene.addRenderable*(new KJS.r.Board*(*)*))
};

var start = function() {

	//KJS.g.setMouseGrab(true);
	
	// Create a new render layer (GKO: Game Kondion Object)
	World.Layers.add(new GKO_Layer(GKO_Layer.RENDER));
	World.Layers.add(new GKO_Layer(GKO_Layer.UPDATE));
	
	SCN.Camera = new OKO_Camera_();
	SCN.Camera.look(0, 0, 5, 0, 0, 0);
	SCN.Camera.setFreeMode(true);
	SCN.Camera.moveSpeed = 10;
	SCN.Camera.s = {
		onupdate: function() {
			//SCN.Ground.rot.x += 0.04;
			//SCN.Ceil.rot.x += 0.04;
		}
	}
	
	// Create a board (RKO: Renderable Kondion Object)
	SCN.Apple = new RKO_Board();
	SCN.Ground = new RKO_InfinitePlane();
	SCN.Ceil = new RKO_InfinitePlane();

	//SCN.Apple.pos.z = -1;
	SCN.Ground.rot.x = 90;
	SCN.Ceil.rot.x = 0;
	SCN.Ground.pos.y = -4;
	SCN.Ceil.pos.z = -10;
	
	World.Layers[0].Apple = SCN.Apple;
	World.Layers[0].Ground = SCN.Ground;
	World.Layers[0].Ceil = SCN.Ceil;
	World.Layers[1].EEE = SCN.Camera;
	World.camera = SCN.Camera;
	
	//SCN.Apple.Seeds = new RKO_Board();
	//SCN.Apple.Seeds.Poison = new RKO_Board();
	
	//SCN.Foo = new RKO_Board();
	//SCN.Foo.Bar = new RKO_Board();
	//SCN.Foo.Bar.HelloWorld = new RKO_Board();
};