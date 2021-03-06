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

var Quaternionf = Java.type("org.joml.Quaternionf");
var Matrix3f = Java.type("org.joml.Matrix3f");
var Matrix2f = Java.type("org.joml.Matrix4f");
var Vector2f = Java.type("org.joml.Vector2f");
var Vector3f = Java.type("org.joml.Vector3f");
var Vector4f = Java.type("org.joml.Vector4f");

var Quaterniond = Java.type("org.joml.Quaterniond");
var Matrix3d = Java.type("org.joml.Matrix3d");
var Matrix2d = Java.type("org.joml.Matrix4d");
var Vector2d = Java.type("org.joml.Vector2d");
var Vector3d = Java.type("org.joml.Vector3d");
var Vector4d = Java.type("org.joml.Vector4d");

var GKO_RenderPass = Java.type("vendalenger.kondion.kobj.GKO_RenderPass");
var GKO_DeferredPass = Java.type("vendalenger.kondion.kobj.GKO_DeferredPass");
var GKO_Scene = Java.type("vendalenger.kondion.kobj.GKO_Scene");

var GKO_Client = Java.type("vendalenger.kondion.kobj.GKO_Client");
var GKO_Server = Java.type("vendalenger.kondion.kobj.GKO_Server");

var NKO_Audio = Java.type("vendalenger.kondion.kobj.NKO_Audio");

var OKO_Camera_ = Java.type("vendalenger.kondion.kobj.OKO_Camera_");

var RKO_Board = Java.type("vendalenger.kondion.kobj.RKO_Board");
var RKO_Obj = Java.type("vendalenger.kondion.kobj.RKO_Obj");
 
var SKO_Cube = Java.type("vendalenger.kondion.kobj.SKO_Cube");
var SKO_InfinitePlane = Java.type("vendalenger.kondion.kobj.SKO_InfinitePlane");

var Mat_FlatColor = Java.type("vendalenger.kondion.materials.KMat_FlatColor");
var Mat_Monotexture = Java.type("vendalenger.kondion.materials.KMat_Monotexture");
var Mat_Strange = Java.type("vendalenger.kondion.materials.KMat_Strange");

var RKO_AmbientLight = Java.type("vendalenger.kondion.kobj.RKO_Light");
var RKO_DirectionalLight = Java.type("vendalenger.kondion.kobj.RKO_DirLight");

var GUI_Button = Java.type("vendalenger.kondion.gui.KButton");
//^kdion.rungamedir (electricfence/kondion.json)
//var KJS = Java.type("vendalenger.kondion.KJS")


var patchObject = function(obj, patch) {
	for (var aname in patch) {
		obj[aname] = patch[aname];
	}
	return obj;
};

var kondionInit = function() {
	SCN.s = {};
	Math.sign = function(a) {
		// This wasn't in nashorn and I considered it important
		return (a == 0) ? 0 : ((a > 0) ? 1 : -1);
	}
	delete kondionInit;
}
