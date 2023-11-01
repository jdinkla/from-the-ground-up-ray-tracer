package net.dinkla.raytracer.swing

import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import javax.swing.JFrame
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.JOptionPane

internal fun createMenuBar(parent: ActionListener): JMenuBar = JMenuBar().apply {
    add(
        JMenu("File").apply {
            mnemonic = KeyEvent.VK_F
            addSeparator()
            add(
                JMenuItem("Quit").apply {
                    mnemonic = KeyEvent.VK_Q
                    addActionListener(parent)
                }
            )
        }
    )
    add(
        JMenu("Help").apply {
            mnemonic = KeyEvent.VK_H
            add(
                JMenuItem("About").apply {
                    mnemonic = KeyEvent.VK_A
                    addActionListener(parent)
                }
            )
        }
    )
}

internal fun about(frame: JFrame) = JOptionPane.showMessageDialog(
    frame,
    informationHeader + '\n' + informationContext,
    informationTitle,
    JOptionPane.PLAIN_MESSAGE
)

internal fun quit(frame: JFrame) {
    val n = JOptionPane.showOptionDialog(
        frame,
        confirmationHeader,
        confirmationTitle,
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE,
        null,
        null,
        null
    )
    if (n == 0) {
        System.exit(0)
    }
}
