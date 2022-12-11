package ImageFilter;

import lwjglutils.OGLBuffers;

public class Grid {

    /**
     * GL_TRIANGLES
     *
     * @param m vertex count in row
     * @param n vertex count in column
     */
    private OGLBuffers buffers;

    public Grid(final int m, final int n) {
        float[] vertices = new float[2 * m * n];
        int[] indices = new int[3 * 2 * (m - 1) * (n - 1)];

        //naplnění vertex bufferu - GL_TRIANGLES
        int verBIndex = 0;
        for (int i = 0; i < m; i++) { //řádky m, X souřadnice
            for (int j = 0; j < n; j++) { //sloupce n, Y souřadnice
                vertices[verBIndex] = j / (float) (n - 1);
                vertices[verBIndex + 1] = i / (float) (m - 1);
                verBIndex += 2;
            }
        }

        //naplnění index bufferu - GL_TRIANGLES
        int offset = 0;
        int indBIndex = 0;
        for (int i = 0; i < m - 1; i++) {
            offset = i * n;
            for (int j = 0; j < n - 1; j++) {     //15/29 prednaska GRID
                indices[indBIndex] = j + offset;
                indices[indBIndex + 1] = j + n + offset;
                indices[indBIndex + 2] = j + 1 + offset;
                indices[indBIndex + 3] = j + 1 + offset;
                indices[indBIndex + 4] = j + n + offset;
                indices[indBIndex + 5] = j + n + 1 + offset;
                indBIndex += 6;
            }
        }

        OGLBuffers.Attrib[] attribs = new OGLBuffers.Attrib[]{
                new OGLBuffers.Attrib("inPosition", 2),
        };

        buffers = new OGLBuffers(vertices, attribs, indices);

    }

    public OGLBuffers getBuffers() {
        return buffers;
    }
}
