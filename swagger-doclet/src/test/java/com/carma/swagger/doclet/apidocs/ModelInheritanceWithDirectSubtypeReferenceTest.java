package com.carma.swagger.doclet.apidocs;

import com.carma.swagger.doclet.DocletOptions;
import com.carma.swagger.doclet.Recorder;
import static com.carma.swagger.doclet.apidocs.FixtureLoader.loadFixture;
import com.carma.swagger.doclet.model.ApiDeclaration;
import com.carma.swagger.doclet.parser.JaxRsAnnotationParser;
import com.sun.javadoc.RootDoc;
import java.io.File;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SuppressWarnings("javadoc")
public class ModelInheritanceWithDirectSubtypeReferenceTest {

    private Recorder recorderMock;
    private DocletOptions options;

    @Before
    public void setup() {
        this.recorderMock = mock(Recorder.class);
        this.options = new DocletOptions().setRecorder(this.recorderMock).setIncludeSwaggerUi(false);
    }

    @Test
    public void testStart() throws IOException {
        final RootDoc rootDoc = RootDocLoader.fromPath("src/test/resources", "fixtures.modelinheritancewithsuperandsubtypereference");
        new JaxRsAnnotationParser(this.options, rootDoc).run();

        final ApiDeclaration api = loadFixture("/fixtures/modelinheritancewithsuperandsubtypereference/modelinheritancewithsuperandsubtypereference.json", ApiDeclaration.class);
        verify(this.recorderMock).record(any(File.class), eq(api));
    }
}
