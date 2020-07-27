
import analyzer.Main;
import org.hyperskill.hstest.stage.StageTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.hyperskill.hstest.testcase.TestCase;

import java.util.List;

class TestCLue {
    String response;
    String feedback;

    TestCLue(String response, String feedback) {
        this.response = response;
        this.feedback = feedback;
    }
}

public class FileTypeAnalyzerTest extends StageTest<TestCLue> {
    public FileTypeAnalyzerTest() {
        super(Main.class);
    }

    @Override
    public List<TestCase<TestCLue>> generate() {
        return List.of(
            new TestCase<TestCLue>()
                .addArguments(new String[]
                    {"doc.pdf", "%PDF-", "PDF document"})
                .addFile("doc.pdf", "PFDF%PDF-PDF")
                .setAttach(new TestCLue("PDF document", "The file had following content: " +
                    "PFDF%PDF-PDF and was analyzed for pattern %PDF-")),

            new TestCase<TestCLue>()
                .addArguments(new String[]
                    {"doc.txt", "%PDF-", "PDF document"})
                .addFile("doc.txt", "PFDF%PDF-PDF")
                .setAttach(new TestCLue("PDF document", "The file had following content: " +
                    "PFDF%PDF-PDF and was analyzed for pattern %PDF-")),

            new TestCase<TestCLue>()
                .addArguments(new String[]
                    {"doc.pdf", "%PDF-", "PDF document"})
                .addFile("doc.pdf", "PFDFPDF")
                .setAttach(new TestCLue("Unknown file type", "The file had following content: " +
                    "PFDFPDF and was analyzed for pattern %PDF-")),

            new TestCase<TestCLue>()
                .addArguments(new String[]
                    {"doc.txt", "%PDF-", "PDF document"})
                .addFile("doc.txt", "PFDFPDF")
                .setAttach(new TestCLue("Unknown file type", "The file had following content: " +
                    "PFDFPDF and was analyzed for pattern %PDF-")),



            new TestCase<TestCLue>()
                .addArguments(new String[]
                    {"doc.pdf", "%DOC-", "DOC document"})
                .addFile("doc.pdf", "PFDF%DOC-PDF")
                .setAttach(new TestCLue("DOC document", "The file had following content: " +
                    "PFDF%PDF-PDF and was analyzed for pattern %PDF-")),

            new TestCase<TestCLue>()
                .addArguments(new String[]
                    {"doc.txt", "%DOC-", "DOC document"})
                .addFile("doc.txt", "PFDF%DOC-PDF")
                .setAttach(new TestCLue("DOC document", "")),

            new TestCase<TestCLue>()
                .addArguments(new String[]
                    {"doc.pdf", "%DOC-", "DOC document"})
                .addFile("doc.pdf", "PFDFPDF")
                .setAttach(new TestCLue("Unknown file type", "")),

            new TestCase<TestCLue>()
                .addArguments(new String[]
                    {"doc.txt", "%DOC-", "DOC document"})
                .addFile("doc.txt", "PFDFPDF")
                .setAttach(new TestCLue("Unknown file type", ""))
        );
    }

    @Override
    public CheckResult check(String reply, TestCLue clue) {
        String actual = reply.strip();
        String expected = clue.response.strip();
        return new CheckResult(actual.equals(expected),
            clue.feedback + "\nExpected result: " + expected +
                "\nActual result: " + actual);
    }
}
