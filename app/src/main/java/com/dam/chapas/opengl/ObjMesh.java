package com.dam.chapas.opengl;

/**
 * @file ObjMesh.java
 * @brief Clase para cargar modelos 3D en formato OBJ
 * @author Andrés Martínez, Ignacio Gómez y Eduardo Díaz
 */

import android.util.Log;
import android.util.Pair;

import com.dam.chapas.app.MainApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @class ObjMesh
 */
public class ObjMesh extends Mesh {

    /**
     * @brief Carga un modelo 3D en formato OBJ
     * @param path          Ruta del archivo
     * @throws IOException  Si no se encuentra el archivo
     */
    public ObjMesh(String path) throws IOException {

        // Abre el archivo
        BufferedReader reader = new BufferedReader(new InputStreamReader(MainApplication.getInstance().getAssets().open(path)));

        // Arrays temporales
        ArrayList<Float> vertices = new ArrayList<>();
        ArrayList<Float> texcoords = new ArrayList<>();
        ArrayList<Float> normals = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();
        HashMap<String, Integer> indicesMap = new HashMap<>();
        HashMap<String, Material> materials = new HashMap<>();
        ArrayList<Float> finalVertices = new ArrayList<>();
        ArrayList<Float> finalTexcoords = new ArrayList<>();
        ArrayList<Float> finalNormals = new ArrayList<>();
        materialGroups = new ArrayList<>();

        // Variables de lectura
        String line;
        int nobjects = 0;
        int nelements = 0;
        int groupStart = -1;
        Material lastMaterial = null;

        // Lee el archivo línea por línea
        while ((line = reader.readLine()) != null) {

            String[] lineTokens = line.split(" ");
            if(lineTokens.length == 0) continue;

            if(lineTokens[0].equals("#")) {
                continue;
            } else if(lineTokens[0].equals("mtllib")) {

                // Ruta de la librería de materiales
                String basepath = "";
                int slashIndex = path.lastIndexOf('/');
                if(slashIndex != -1) {
                    basepath = path.substring(0, slashIndex + 1);
                }
                this.loadMtl(basepath + lineTokens[1], materials);
            } else if(lineTokens[0].equals("o")) {

                // Nuevo objeto
                if(nobjects > 0) {
                    throw new RuntimeException("More than one object in ObjMesh: " + path);
                } else {
                    nobjects ++;
                }
            } else if(lineTokens[0].equals("v")) {

                // Nuevo vértice
                vertices.add(Float.parseFloat(lineTokens[1]));
                vertices.add(Float.parseFloat(lineTokens[2]));
                vertices.add(Float.parseFloat(lineTokens[3]));
            } else if(lineTokens[0].equals("vt")) {

                // Nueva coordenada de textura
                texcoords.add(Float.parseFloat(lineTokens[1]));
                texcoords.add(1.0f - Float.parseFloat(lineTokens[2]));
            } else if(lineTokens[0].equals("vn")) {

                // Nueva normal
                normals.add(Float.parseFloat(lineTokens[1]));
                normals.add(Float.parseFloat(lineTokens[2]));
                normals.add(Float.parseFloat(lineTokens[3]));
            } else if(lineTokens[0].equals("f")) {

                // Nuevo triángulo
                for(int i = 0; i < 3; i++) {
                    String token = lineTokens[i + 1];
                    if(indicesMap.containsKey(token)) {
                        indices.add(indicesMap.get(token));
                    } else {
                        indices.add(indicesMap.size());
                        indicesMap.put(token, indicesMap.size());
                        String[] subtoken = token.split("/");
                        Integer vertex = Integer.parseInt(subtoken[0]) - 1;
                        Integer texcoord = Integer.parseInt(subtoken[1]) - 1;
                        Integer normal = Integer.parseInt(subtoken[2]) - 1;
                        for(int j = 0; j < 3; j++) {
                            finalVertices.add(vertices.get(vertex * 3 + j));
                        }
                        for(int j = 0; j < 2; j++) {
                            finalTexcoords.add(texcoords.get(texcoord * 2 + j));
                        }
                        for(int j = 0; j < 3; j++) {
                            finalNormals.add(normals.get(normal * 3 + j));
                        }
                    }
                }
                nelements += 3;
            } else if(lineTokens[0].equals("usemtl")) {

                // Nuevo grupo de material
                if(groupStart == -1) {
                    groupStart = nelements;
                    lastMaterial = materials.get(lineTokens[1]);
                } else {
                    materialGroups.add(new Pair<Integer, Material>(nelements, lastMaterial));
                    lastMaterial = materials.get(lineTokens[1]);
                }
            }
        }

        // Añade el último grupo de material
        materialGroups.add(new Pair<Integer, Material>(nelements, lastMaterial));

        // Cierra el archivo
        reader.close();

        // Convierte las listas en arrays
        float[] verticesArray = new float[finalVertices.size()];
        float[] texcoordsArray = new float[finalTexcoords.size()];
        float[] normalsArray = new float[finalNormals.size()];
        for(int i = 0; i < verticesArray.length; i++) {
            verticesArray[i] = finalVertices.get(i);
        }
        for(int i = 0; i < texcoordsArray.length; i++) {
            texcoordsArray[i] = finalTexcoords.get(i);
        }
        for(int i = 0; i < normalsArray.length; i++) {
            normalsArray[i] = finalNormals.get(i);
        }

        // Crea los VBO
        vbos = new VBO[3];
        vbos[0] = new VBO(verticesArray, VBO.VERTICES);
        vbos[1] = new VBO(texcoordsArray, VBO.TEXCOORDS);
        vbos[2] = new VBO(normalsArray, VBO.NORMALS);

        // Crea el IBO
        short[] indicesArray = new short[indices.size()];
        for(int i = 0; i < indicesArray.length; i++) {
            indicesArray[i] = indices.get(i).shortValue();
        }
        ibo = new IBO(indicesArray);
    }

    /**
     * @brief Carga una librería de materiales en formato MTL
     * @param path          Ruta del archivo MTL
     * @param materials     Lista de materiales (salida)
     * @throws IOException  Si no se encuentra el archivo
     */
    private void loadMtl(String path, HashMap<String, Material> materials) throws IOException {

        // Abre el archivo
        BufferedReader reader = new BufferedReader(new InputStreamReader(MainApplication.getInstance().getAssets().open(path)));

        // Variables de lectura
        String line;
        Material curMaterial = null;

        // Lee el archivo línea por línea
        while ((line = reader.readLine()) != null) {

            String[] lineTokens = line.split(" ");
            if(lineTokens.length == 0) continue;

            if(lineTokens[0].equals("#")) {
                continue;
            } else if(lineTokens[0].equals("newmtl")) {
                curMaterial = new Material();
                materials.put(lineTokens[1], curMaterial);
            } else if(lineTokens[0].equals("Ka")) {
                curMaterial.setAmbient(Float.parseFloat(lineTokens[1]), Float.parseFloat(lineTokens[2]), Float.parseFloat(lineTokens[3]));
            } else if(lineTokens[0].equals("Kd")) {
                curMaterial.setDiffuse(Float.parseFloat(lineTokens[1]), Float.parseFloat(lineTokens[2]), Float.parseFloat(lineTokens[3]));
            } else if(lineTokens[0].equals("Ks")) {
                curMaterial.setSpecular(Float.parseFloat(lineTokens[1]), Float.parseFloat(lineTokens[2]), Float.parseFloat(lineTokens[3]));
            } else if(lineTokens[0].equals("Ke")) {
                curMaterial.setEmissive(Float.parseFloat(lineTokens[1]), Float.parseFloat(lineTokens[2]), Float.parseFloat(lineTokens[3]));
            } else if(lineTokens[0].equals("d")) {
                curMaterial.setAlpha(Float.parseFloat(lineTokens[1]));
            } else if(lineTokens[0].equals("map_Kd")) {
                curMaterial.setTexture(new Texture("texture/" + lineTokens[1]));
            } else if(lineTokens[0].equals("Ns")) {
                curMaterial.setShininess(Float.parseFloat(lineTokens[1]));
            }
        }

        // Guarda los materiales
        this.materials = new Material[materials.size()];
        int i = 0;
        for(Map.Entry<String, Material> m : materials.entrySet()) {
            this.materials[i] = m.getValue();
            i++;
        }

        // Cierra el archivo
        reader.close();
    }
}
