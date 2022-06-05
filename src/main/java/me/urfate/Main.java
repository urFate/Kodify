package me.urfate;

import me.urfate.util.FileUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import static me.urfate.util.FileUtil.getFileExtension;

public class Main {
    private static OptionsParser optionsParser;

    private static String path;
    private static String name;
    private static int season;

    public static void main(String[] args) {
        optionsParser = new OptionsParser(args);

        // Register CLI options
        registerOptions();

        // Check CLI options and define variables
        checkOptions();

        // Affect only video files
        FileFilter videoFilter = FileUtil::isVideo;

        // Sort our videos in alphabetical order
        List<File> videos = Arrays.stream(new File(path).listFiles(videoFilter)).sorted().collect(Collectors.toList());

        // Exit if we can't find any videos in provided directory
        if(videos.size() == 0){
            System.out.printf("No videos found in directory \"%s\"%n", path);
            System.exit(1);
        }

        // Add all videos to our HashMap with their original file name and new name
        LinkedHashMap<File, String> queue = new LinkedHashMap<>();

        for (int i = 0; i < videos.size(); i++) {
            File video = videos.get(i);
            String videoName = video.getName();
            String extension = getFileExtension(video);
            int episode = i+1;

            String newName = String.format("%s S%sE%s%s", name, season, episode, extension);

            queue.put(video, newName);

            // Preview result
            System.out.printf("%s -> %s%n", videoName, newName);
        }

        // Ask for confirmation
        System.out.println("It's ok? [Y/n]");

        String answer = System.console().readLine();

        // Rename our videos
        if(answer.equalsIgnoreCase("y")){
            queue.forEach((video, newName) -> {
                try {
                    Path source = Paths.get(video.getPath());
                    Files.move(source, source.resolveSibling(newName));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            System.out.println("Success!");
        } else {
            System.out.println("Operation interrupted.");
            System.exit(0);
        }
    }

    private static void registerOptions(){
        optionsParser.registerOption("p", "path", true, "Path to videos folder");
        optionsParser.registerOption("n", "name", true, "Name of TV-Show");
        optionsParser.registerOption("s", "season", true, "TV-Show season");
    }

    private static void checkOptions(){
        path = optionsParser.parseOption("p");

        if(path == null){
            optionsParser.printHelp();
            System.exit(0);
        }

        if(!Files.exists(Paths.get(path))){
            System.out.printf("Path \"%s\" not found.%n", path);
            System.exit(1);
        }

        // Use parent directory name if title is not provided
        name = optionsParser.parseOption("n") != null ? optionsParser.parseOption("n") : new File(path).getParentFile().getName();

        try{
            season = optionsParser.parseOption("s") != null ? Integer.parseInt(optionsParser.parseOption("s")) : 1;
        } catch (NumberFormatException e){
            System.out.printf("Invalid number format \"%s\".", optionsParser.parseOption("s"));
            System.exit(1);
        }
    }
}