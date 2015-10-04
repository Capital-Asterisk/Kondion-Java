/* MORVID
 * THE KONDION masterscript.js
 * Contains everything about Morvid
 *
 *
 *
 */

var init = function() {
	KJS.java.issueCommand("^eggs");
};

var start = function() {
	KJS.g.setMouseGrab(true);
	var player = KJS.e.spawnEnt("mv_player", {});
	player.mouseRotate = true;
	//KJS.c.freeCam(true);
	KJS.c.bindCam(player.obj);
	KJS.kondion.getCurrentCamera().lockRotation(player.obj.getRotation());
	//KJS.s.newAABlockCS(new Vector3f(0, 0, 0), true, 2, 7, 1, 8, 8, 8, 8);
	//KJS.s.newAABlockCS(new Vector3f(0, 0, 0), true, 2, 5, 0, 20, 2, 2, 2);
	//KJS.s.newAABlockCS(new Vector3f(0, 0, 0), true, 2, 1, 5, 9, 2, 2, 2);
	//KJS.s.newAABlockCS(new Vector3f(-1, 0, 0), true, 2, 4, 0.5, 12, 4, 12, 4);
	//KJS.s.newAABlockCS(new Vector3f(-2, 4, 20), true, 2, 1, 15, 8, 8, 8, 8);
	KJS.s.newAABlockCS(new Vector3f(0, -20, 1), true, 2, 1, 3, 8, 8, 8, 8);
};

KJS.e.rEnt({
	id: "mv_player",
	name: "You",
	traits: ["et_alive", "ph_gravity"],
	tickInterval: 1,
	create: function() {
		
	},
	notify: function(msg) {
		
	},
	tick: function() {
		if (KJS.i.buttonDown(KJS.b.open)) {
			this.obj.getVelocity().y = 3;
			
			
		}
		if (KJS.i.buttonDown(KJS.b.up)) {
			this.obj.thrustYAngle(this.obj.getRotation().x - Math.PI, 0.5, 10);
		} else {
			this.obj.velocity.set(0, this.obj.velocity.y, 0);
		}
		if (KJS.i.buttonDown(KJS.b.down)) {
			print("(" + this.obj.position.x + ", " + this.obj.position.y + ", " + this.obj.position.z + ")");
		}
	}
});