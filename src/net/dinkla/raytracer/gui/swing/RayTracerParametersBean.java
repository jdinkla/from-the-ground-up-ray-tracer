package net.dinkla.raytracer.gui.swing;

import net.dinkla.raytracer.math.Point3D;
import net.dinkla.raytracer.math.Vector3D;

public class RayTracerParametersBean {
    private Float eyeX;
    private Float eyeY;
    private Float eyeZ;
    private Float lookAtX;
    private Float lookAtY;
    private Float lookAtZ;
    private Float upZ;
    private Float upY;
    private Float upX;
    private Float d;
    private Integer numProcessors;
    private Integer hres;
    private Float size;
    private Float gamma;
    private Integer numSamples;
    private Integer maxDepth;
    private Boolean showOutOfGamut;
    private Integer vres;
    private String fileName;
    private String worldProgram;
    private Float exposureTime;
    
    public RayTracerParametersBean() {
        eyeX = 0.0f;
        eyeY = 0.0f;
        eyeZ = 0.0f;
        lookAtX = 0.0f;
        lookAtY = 0.0f;
        lookAtZ = 0.0f;
        upZ = 0.0f;
        upY = 0.0f;
        upX = 0.0f;
        d  = 0.0f;
        numProcessors = Runtime.getRuntime().availableProcessors();
        hres = 480 * 9/16;
        vres = 480;
        size = 1.0f;
        gamma = 1.0f;
        numSamples = 0;
        maxDepth = 5;
        showOutOfGamut = false;
        fileName = "";
        worldProgram = "";
        exposureTime = 1.0f;
    }

    public void setEye(Point3D eye) {
        eyeX = eye.getX();
        eyeY = eye.getY();
        eyeZ = eye.getZ();
    }

    public void setEye(float x, float y, float z) {
        eyeX = x;
        eyeY = y;
        eyeZ = z;
    }

    public Point3D getEye() {
        return new Point3D(eyeX, eyeY, eyeZ);
    }

    public void setLookAt(Point3D lookAt) {
        lookAtX = lookAt.getX();
        lookAtY = lookAt.getY();
        lookAtZ = lookAt.getZ();
    }

    public void setLookAt(float x, float y, float z) {
        lookAtX = x;
        lookAtY = y;
        lookAtZ = z;
    }

    public Point3D getLookAt() {
        return new Point3D(lookAtX, lookAtY, lookAtZ);
    }

    public void setUp(Point3D up) {
        upX = up.getX();
        upY = up.getY();
        upZ = up.getZ();
    }

    public void setUp(float x, float y, float z) {
        upX = x;
        upY = y;
        upZ = z;
    }

    public Vector3D getUp() {
        return new Vector3D(upX, upY, upZ);
    }
    
    public Float getEyeX() {
        return eyeX;
    }

    public void setEyeX(final Float eyeX) {
        this.eyeX = eyeX;
    }

    public Float getEyeY() {
        return eyeY;
    }

    public void setEyeY(final Float eyeY) {
        this.eyeY = eyeY;
    }

    public Float getEyeZ() {
        return eyeZ;
    }

    public void setEyeZ(final Float eyeZ) {
        this.eyeZ = eyeZ;
    }

    public Float getLookAtX() {
        return lookAtX;
    }

    public void setLookAtX(final Float lookAtX) {
        this.lookAtX = lookAtX;
    }

    public Float getLookAtY() {
        return lookAtY;
    }

    public void setLookAtY(final Float lookAtY) {
        this.lookAtY = lookAtY;
    }

    public Float getLookAtZ() {
        return lookAtZ;
    }

    public void setLookAtZ(final Float lookAtZ) {
        this.lookAtZ = lookAtZ;
    }

    public Float getUpZ() {
        return upZ;
    }

    public void setUpZ(final Float upZ) {
        this.upZ = upZ;
    }

    public Float getUpY() {
        return upY;
    }

    public void setUpY(final Float upY) {
        this.upY = upY;
    }

    public Float getUpX() {
        return upX;
    }

    public void setUpX(final Float upX) {
        this.upX = upX;
    }

    public Float getD() {
        return d;
    }

    public void setD(final Float d) {
        this.d = d;
    }

    public Integer getNumProcessors() {
        return numProcessors;
    }

    public void setNumProcessors(final Integer numProcessors) {
        this.numProcessors = numProcessors;
    }

    public Integer getHres() {
        return hres;
    }

    public void setHres(final Integer hres) {
        this.hres = hres;
    }

    public Float getSize() {
        return size;
    }

    public void setSize(final Float size) {
        this.size = size;
    }

    public Float getGamma() {
        return gamma;
    }

    public void setGamma(final Float gamma) {
        this.gamma = gamma;
    }

    public Integer getNumSamples() {
        return numSamples;
    }

    public void setNumSamples(final Integer numSamples) {
        this.numSamples = numSamples;
    }

    public Integer getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(final Integer maxDepth) {
        this.maxDepth = maxDepth;
    }

    public Boolean isShowOutOfGamut() {
        return showOutOfGamut;
    }

    public void setShowOutOfGamut(final Boolean showOutOfGamut) {
        this.showOutOfGamut = showOutOfGamut;
    }

    public Integer getVres() {
        return vres;
    }

    public void setVres(final Integer vres) {
        this.vres = vres;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    public String getWorldProgram() {
        return worldProgram;
    }

    public void setWorldProgram(final String worldProgram) {
        this.worldProgram = worldProgram;
    }

    public Float getExposureTime() {
        return exposureTime;
    }

    public void setExposureTime(Float exposureTime) {
        this.exposureTime = exposureTime;
    }
}

