package net.dinkla.raytracer.gui.swing;

import net.dinkla.raytracer.math.Point3D;
import net.dinkla.raytracer.math.Vector3D;

public class RayTracerParametersBean {
    private Double eyeX;
    private Double eyeY;
    private Double eyeZ;
    private Double lookAtX;
    private Double lookAtY;
    private Double lookAtZ;
    private Double upZ;
    private Double upY;
    private Double upX;
    private Double d;
    private Integer numProcessors;
    private Integer hres;
    private Double size;
    private Double gamma;
    private Integer numSamples;
    private Integer maxDepth;
    private Boolean showOutOfGamut;
    private Integer vres;
    private String fileName;
    private String worldProgram;
    private Double exposureTime;
    
    public RayTracerParametersBean() {
        eyeX = 0.0;
        eyeY = 0.0;
        eyeZ = 0.0;
        lookAtX = 0.0;
        lookAtY = 0.0;
        lookAtZ = 0.0;
        upZ = 0.0;
        upY = 0.0;
        upX = 0.0;
        d  = 0.0;
        numProcessors = Runtime.getRuntime().availableProcessors();
        hres = 480 * 9/16;
        vres = 480;
        size = 1.0;
        gamma = 1.0;
        numSamples = 0;
        maxDepth = 5;
        showOutOfGamut = false;
        fileName = "";
        worldProgram = "";
        exposureTime = 1.0;
    }

    public void setEye(Point3D eye) {
        eyeX = eye.getX();
        eyeY = eye.getY();
        eyeZ = eye.getZ();
    }

    public void setEye(Double x, Double y, Double z) {
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

    public void setLookAt(Double x, Double y, Double z) {
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

    public void setUp(Double x, Double y, Double z) {
        upX = x;
        upY = y;
        upZ = z;
    }

    public Vector3D getUp() {
        return new Vector3D(upX, upY, upZ);
    }
    
    public Double getEyeX() {
        return eyeX;
    }

    public void setEyeX(final Double eyeX) {
        this.eyeX = eyeX;
    }

    public Double getEyeY() {
        return eyeY;
    }

    public void setEyeY(final Double eyeY) {
        this.eyeY = eyeY;
    }

    public Double getEyeZ() {
        return eyeZ;
    }

    public void setEyeZ(final Double eyeZ) {
        this.eyeZ = eyeZ;
    }

    public Double getLookAtX() {
        return lookAtX;
    }

    public void setLookAtX(final Double lookAtX) {
        this.lookAtX = lookAtX;
    }

    public Double getLookAtY() {
        return lookAtY;
    }

    public void setLookAtY(final Double lookAtY) {
        this.lookAtY = lookAtY;
    }

    public Double getLookAtZ() {
        return lookAtZ;
    }

    public void setLookAtZ(final Double lookAtZ) {
        this.lookAtZ = lookAtZ;
    }

    public Double getUpZ() {
        return upZ;
    }

    public void setUpZ(final Double upZ) {
        this.upZ = upZ;
    }

    public Double getUpY() {
        return upY;
    }

    public void setUpY(final Double upY) {
        this.upY = upY;
    }

    public Double getUpX() {
        return upX;
    }

    public void setUpX(final Double upX) {
        this.upX = upX;
    }

    public Double getD() {
        return d;
    }

    public void setD(final Double d) {
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

    public Double getSize() {
        return size;
    }

    public void setSize(final Double size) {
        this.size = size;
    }

    public Double getGamma() {
        return gamma;
    }

    public void setGamma(final Double gamma) {
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

    public Double getExposureTime() {
        return exposureTime;
    }

    public void setExposureTime(Double exposureTime) {
        this.exposureTime = exposureTime;
    }
}

