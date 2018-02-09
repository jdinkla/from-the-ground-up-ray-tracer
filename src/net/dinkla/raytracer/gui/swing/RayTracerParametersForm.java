package net.dinkla.raytracer.gui.swing;

import net.dinkla.raytracer.cameras.Camera;
import net.dinkla.raytracer.cameras.render.ISingleRayRenderer;
import net.dinkla.raytracer.cameras.render.IRenderer;
import net.dinkla.raytracer.cameras.render.ParallelRenderer;
import net.dinkla.raytracer.cameras.render.SimpleRenderer;
import net.dinkla.raytracer.utilities.Resolution;
import net.dinkla.raytracer.ViewPlane;
import net.dinkla.raytracer.films.IFilm;
import net.dinkla.raytracer.cameras.lenses.Pinhole;
import net.dinkla.raytracer.tracers.RayCast;
import net.dinkla.raytracer.tracers.Tracer;
import net.dinkla.raytracer.worlds.World;
import net.dinkla.raytracer.worlds.WorldBuilder;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 15.04.2010
 * Time: 20:53:05
 * To change this template use File | Settings | File Templates.
 */
public class RayTracerParametersForm {
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JTextField textFileName;
    private JEditorPane editorPaneWorld;
    private JTextField hres;
    private JTextField size;
    private JTextField gamma;
    private JTextField numSamples;
    private JTextField maxDepth;
    private JCheckBox showOutOfGamut;
    private JTextField vres;
    private JTextField eyeX;
    private JTextField eyeY;
    private JTextField eyeZ;
    private JTextField lookAtX;
    private JTextField lookAtY;
    private JTextField lookAtZ;
    private JTextField upZ;
    private JTextField upY;
    private JTextField upX;
    private JTextField d;
    private JTextField numThreads;
    private JTextField exposureTime;
    private JButton chooseFileButton;
    private JButton quitButton;
    private JButton saveButton;
    private JButton applyButton;
    private JComboBox comboBoxCameraType;
    private JComboBox comboBoxViewPlaneSampler;
    private JComboBox comboBoxTracer;
    
    final private RayTracerParametersBean bean;

    public RayTracerParametersForm() {
        bean = new RayTracerParametersBean();
        applyButton.addActionListener(e -> {
            if (bean != null) {
                getData(bean);

                File file = new File(bean.getFileName());
                if (!file.exists()) {
                    JOptionPane.showMessageDialog(null, "File '" + bean.getFileName() +  "' does not exists");
                } else {
                    try {
                        World world = WorldBuilder.create(file);

                        final RenderGui renderGui1 = new RenderGui(world);
                        RenderGui renderGui = renderGui1.invoke();
                        World w = renderGui.getW();
                        Camera camera = renderGui.getCamera();
                        ImageFrame imf = renderGui.getImf();

                        w.initialize();
                        camera.render((IFilm) imf, 0);
                        imf.repaint();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Exception: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }
        });
        quitButton.addActionListener(e -> System.exit(0));
        saveButton.addActionListener(e -> {
            //To change body of implemented methods use File | Settings | File Templates.
        });
        chooseFileButton.addActionListener(e -> {
            final JFileChooser fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(panel1);
            if (returnVal == 0) {
                File file = fc.getSelectedFile();
                getData(bean);
                bean.setFileName(file.getAbsolutePath());
                setData(bean);
            }
        });
    }

    public void setData(RayTracerParametersBean data) {
        eyeX.setText(String.valueOf(data.getEyeX()));
        eyeY.setText(String.valueOf(data.getEyeY()));
        eyeZ.setText(String.valueOf(data.getEyeZ()));
        lookAtX.setText(String.valueOf(data.getLookAtX()));
        lookAtY.setText(String.valueOf(data.getLookAtY()));
        lookAtZ.setText(String.valueOf(data.getLookAtZ()));
        upZ.setText(String.valueOf(data.getUpZ()));
        upY.setText(String.valueOf(data.getUpY()));
        upX.setText(String.valueOf(data.getUpX()));
        d.setText(String.valueOf(data.getD()));
        numThreads.setText(String.valueOf(data.getNumProcessors()));
        hres.setText(String.valueOf(data.getHres()));
        size.setText(String.valueOf(data.getSize()));
        gamma.setText(String.valueOf(data.getGamma()));
        numSamples.setText(String.valueOf(data.getNumSamples()));
        maxDepth.setText(String.valueOf(data.getMaxDepth()));
        showOutOfGamut.setSelected(data.isShowOutOfGamut());
        vres.setText(String.valueOf(data.getVres()));
        textFileName.setText(String.valueOf(data.getFileName()));
        //editorPaneWorld.setText(data.getWorldProgram());
        exposureTime.setText(String.valueOf(data.getExposureTime()));
//        comboBoxCameraType.setSelectedIndex(0);
//        comboBoxViewPlaneSampler.setSelectedIndex(0);
//        comboBoxTracer.setSelectedIndex(0);
    }

    public void getData(RayTracerParametersBean data) {
        data.setEyeX(Double.valueOf(eyeX.getText()));
        data.setEyeY(Double.valueOf(eyeY.getText()));
        data.setEyeZ(Double.valueOf(eyeZ.getText()));
        data.setLookAtX(Double.valueOf(lookAtX.getText()));
        data.setLookAtY(Double.valueOf(lookAtY.getText()));
        data.setLookAtZ(Double.valueOf(lookAtZ.getText()));
        data.setUpZ(Double.valueOf(upZ.getText()));
        data.setUpY(Double.valueOf(upY.getText()));
        data.setUpX(Double.valueOf(upX.getText()));
        data.setD(Double.valueOf(d.getText()));
        data.setNumProcessors(Integer.valueOf(numThreads.getText()));
        data.setHres(Integer.valueOf(hres.getText()));
        data.setSize(Double.valueOf(size.getText()));
        data.setGamma(Double.valueOf(gamma.getText()));
        data.setNumSamples(Integer.valueOf(numSamples.getText()));
        data.setMaxDepth(Integer.valueOf(maxDepth.getText()));
        data.setShowOutOfGamut(showOutOfGamut.isSelected());
        data.setVres(Integer.valueOf(vres.getText()));
        data.setFileName(textFileName.getText());
//        data.setWorldProgram(editorPaneWorld.getText());
        data.setExposureTime(Double.valueOf(exposureTime.getText()));
    }

    public boolean isModified(RayTracerParametersBean data) {
        if (eyeX.getText() != null ? !eyeX.getText().equals(data.getEyeX()) : data.getEyeX() != null) return true;
        if (eyeY.getText() != null ? !eyeY.getText().equals(data.getEyeY()) : data.getEyeY() != null) return true;
        if (eyeZ.getText() != null ? !eyeZ.getText().equals(data.getEyeZ()) : data.getEyeZ() != null) return true;
        if (lookAtX.getText() != null ? !lookAtX.getText().equals(data.getLookAtX()) : data.getLookAtX() != null)
            return true;
        if (lookAtY.getText() != null ? !lookAtY.getText().equals(data.getLookAtY()) : data.getLookAtY() != null)
            return true;
        if (lookAtZ.getText() != null ? !lookAtZ.getText().equals(data.getLookAtZ()) : data.getLookAtZ() != null)
            return true;
        if (upZ.getText() != null ? !upZ.getText().equals(data.getUpZ()) : data.getUpZ() != null) return true;
        if (upY.getText() != null ? !upY.getText().equals(data.getUpY()) : data.getUpY() != null) return true;
        if (upX.getText() != null ? !upX.getText().equals(data.getUpX()) : data.getUpX() != null) return true;
        if (d.getText() != null ? !d.getText().equals(data.getD()) : data.getD() != null) return true;
        if (numThreads.getText() != null ? !numThreads.getText().equals(data.getNumProcessors()) : data.getNumProcessors() != null)
            return true;
        if (hres.getText() != null ? !hres.getText().equals(data.getHres()) : data.getHres() != null) return true;
        if (size.getText() != null ? !size.getText().equals(data.getSize()) : data.getSize() != null) return true;
        if (gamma.getText() != null ? !gamma.getText().equals(data.getGamma()) : data.getGamma() != null) return true;
        if (numSamples.getText() != null ? !numSamples.getText().equals(data.getNumSamples()) : data.getNumSamples() != null)
            return true;
        if (maxDepth.getText() != null ? !maxDepth.getText().equals(data.getMaxDepth()) : data.getMaxDepth() != null)
            return true;
        if (showOutOfGamut.isSelected() != data.isShowOutOfGamut()) return true;
        if (vres.getText() != null ? !vres.getText().equals(data.getVres()) : data.getVres() != null) return true;
        if (textFileName.getText() != null ? !textFileName.getText().equals(data.getFileName()) : data.getFileName() != null)
            return true;
        if (editorPaneWorld.getText() != null ? !editorPaneWorld.getText().equals(data.getWorldProgram()) : data.getWorldProgram() != null)
            return true;
        return false;
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("RayTracerParametersForm");
        RayTracerParametersForm form = new RayTracerParametersForm();
        frame.setContentPane(form.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();

        // Initialize Bean
        form.bean.setEye(-10.0, 3.0, 10.0);
        form.bean.setLookAt(0.0, 1.0, 0.0);
        form.bean.setUp(0.0, 1.0, 0.0);
        form.bean.setD(1000.0);
        form.bean.setExposureTime(1.0);

        form.bean.setGamma(1.0);
      
        form.bean.setHres(640);
        form.bean.setVres(480);

        form.setData(form.bean);

        frame.setVisible(true);
    }

    public class ClickAction extends AbstractAction {
        private JButton button;
        public ClickAction(JButton button) {
            this.button = button;
        }
        public void actionPerformed(ActionEvent e) {
            button.doClick();
        }
    }

    private void createUIComponents() {
        applyButton = new JButton();
        InputMap inputMap = applyButton.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW);
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        inputMap.put(enter, "ENTER");
        applyButton.getActionMap().put("ENTER", new ClickAction(applyButton));

        quitButton = new JButton();
        inputMap = quitButton.getInputMap(JButton.WHEN_IN_FOCUSED_WINDOW);
        KeyStroke esc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        inputMap.put(esc, "ESC");
        quitButton.getActionMap().put("ESC", new ClickAction(quitButton));
    }

    private class RenderGui {
        private World w;
        private ImageFrame imf;
        private Camera camera;

        public World getW() {
            return w;
        }

        private RenderGui(World w) {
            this.w = w;
        }

        public ImageFrame getImf() {
            return imf;
        }

        public Camera getCamera() {
            return camera;
        }

        public RenderGui invoke() {
            // ViewPlane
            ViewPlane vp = new ViewPlane();
            vp.resolution = new Resolution(bean.getHres(), bean.getVres());
//            vp.numSamples = bean.getNumSamples();
//            vp.sampler = new Sampler(new PureRandom(), 100, 10);
            //vp.sampler = new Constant2D(0.5);
            vp.size = bean.getSize();
            vp.maxDepth = bean.getMaxDepth();
            vp.setGamma(bean.getGamma());
            vp.showOutOfGamut = bean.isShowOutOfGamut();

            imf = new ImageFrame(vp.resolution, false, bean);

            Tracer tracer = new RayCast(this.w);
            //Tracer tracer = new AreaLighting(world);

            // TODO why is lens a pinhole?
            Pinhole lens = new Pinhole(vp);
            lens.setD(bean.getD());
            ISingleRayRenderer render = new SimpleRenderer(lens, tracer);
            IRenderer render2 = new ParallelRenderer(render, vp);
            camera = new Camera(lens, render2);
            camera.setup(bean.getEye(), bean.getLookAt(), bean.getUp());
            //camera.exposureTime = bean.getExposureTime();
            return this;
        }
    }
}
