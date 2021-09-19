# MIDISense | MIDI FIle Interpretation

---

This system enables the interpretation, display, playback and analysis of musical data stored in a midi format.

Music forms an integral part of modern society. 
It is without a doubt, the most ubiquitous of art forms, permeating through the day-to-day lives of most individuals and is one that most people would find challenging to live without.


![Alt text](documentation/images/Midi_final.png?raw=true "MIDISense Logo")

As with most artistic disciplines, the accessibility of music and a rise in music education among the youth have made its practises an appropriate target for rapid digitisation. As the composition, arrangement and transcription of works are common industry practises, there has been a drive in recent years to marry these processes with technologies that enhance both the sensory and extrasensory perception of a work during such tasks.

Our system aims to enhance such practises by presenting MIDI data in a format that is simple to understand and is visually appealing, while capturing technically meaningful information, such as:

- The Key Signature, Time Signature and Tempo Indication of a MIDI file.
- The Timing, Velocity, Pitch, Instrument and Duration elements of a specific MIDI channel in a file.
- An AI-generated classification of a genre potentially associated with the contents of a MIDI file.

Such that novices and experts alike can find some value in the resultant musicological analysis.

# Version Changelog #

---
## + Version 1.0.0  | <b style="color:green">Demo 4</b> standard
#### New Additions and Fixes: ####


- Added time-series elements, such as time, tempo, and key signature changes. Hover over note groups to get a detailed description of the piece metadata at that time.
- Added tempo-sensitivity to detect gradual changes in tempo like ritardandos and accelerandos.
- Added chord and interval analysis. Hover of note groups to detect the group type.
- Added live analysis mode:
  - Allows one to record a sequence of specified length and BPM, with an onscreen keyboard. Keymaps/mouse clicks are used to play the keyboard.
  - Allows for the adjustment of the output instrument sound.
  - Allows editing/addition of content with a piano roll component.
  - Allows the painting of piano roll element by toggling with shift key.
  - Allows you to send a recorded sequence to the analysis engine.
  - Allows for the adjustment of the input keyboard mode.
- Added comprehensive help and about views.
- New distributed interpretation architecture, powered by mido.
- New broker architecture for interpretation requests.
- New deamon to enforce file retention policy.
- New request monitoring for interpretation requests.
- Improved efficiency of local file storage. Interpreted files do not remain in local storage.

### Notes and Resources ###
For a detailed description of the system, its requirement satisfactions and architecture, consult the following resources.
* :open_book: [Software Requirements Specification](https://www.overleaf.com/read/qsxnfqfpwsjx)
* :open_book: [Architectural Requirements](https://www.overleaf.com/read/cxcxhxdqwvcm )
* :open_book: [Coding Quality Document](https://www.overleaf.com/read/nnvdgdcnctyk)
* :open_book: [Testing Policy](https://drive.google.com/file/d/133imQBbwgno3LWMEDwUKvRmF4CTsVs7U/view?usp=sharing)

For a detailed description of how to install and use the service, see the resources below.
* :open_book: [Installation Manual](https://www.overleaf.com/read/wqzgbdtyczry)
* :open_book: [User Manual](https://www.overleaf.com/read/qvwdczpqtwmk)


### Demonstration ###

For a run-through of the changes made since Demo 3, consult the following demonstration recording.

* :tv: [Demonstration 4](https://drive.google.com/file/d/19XxAZ-36f_93X-xQWs0fHf8Z1O3AfQsu/view)

## + Version 1.0.0  | <b style="color:green">Demo 3</b> standard 
#### New Additions and Fixes: ####

- Added first prototype user interface.
- Track information is presented chronologically instead of sequentially.
- Genre classifications are presented dynamically in a radar chart.
- Added first prototype AI to classify genres.
- Tooltip information is provided for note pitches.
- Graphical display for piece metadata like key and time signatures.
- Added a note inspector to represent technical elements, in favour of a tooltip.
- Added compression to greatly improve the speed of interpretation.
- Fixed routing issues between pages.
- Fixed uploading issues.
- Tidied up file structure
- Added more comprehensive tests.
- Added more descriptive comments.

### Notes and Resources ###
For a detailed description of the system, its requirement satisfactions and architecture, consult the following resources.
* :open_book: [Software Requirements Specification](https://www.overleaf.com/read/pzxnwzmfwbnx)
* :open_book: [Architectural Requirements](https://www.overleaf.com/read/rnbghzsyvqjv )
* :open_book: [Coding Quality Document](https://www.overleaf.com/read/tkkkdqytzktv)

For a detailed description of how to install and use the service, see the resources below.
* :open_book: [Installation Manual](https://www.overleaf.com/read/djmbjfwhwbbp)
* :open_book: [User Manual](https://www.overleaf.com/read/ddyfqzkcspzd)


### Demonstration ###

For a run-through of the changes made since Demo 2, consult the following demonstration recording.

* :tv: [Demonstration 3](https://drive.google.com/file/d/19XxAZ-36f_93X-xQWs0fHf8Z1O3AfQsu/view)

## + Version 1.0.0 | <b style="color:teal">Demo 2</b> standard
#### New Additions and Fixes: ####

- Track interpretation added.
- Added File Upload service.
- Controller layer and endpoints added.
- Added methods for displaying data.

### Notes and Resources ###
For a detailed description of the system, its requirement satisfactions and architecture, consult the following resources.
* :open_book: [Software Requirements Specification](https://www.overleaf.com/read/vcghdpcsjqnj)
* :open_book: [Architectural Requirements](https://www.overleaf.com/read/wtvwkjzrvvrm)
* :open_book: [Coding Quality Document](https://www.overleaf.com/read/cyzqjtvqthwg )

For a detailed description of how to use the service, see the resources below.
* :open_book: [User Manual](https://www.overleaf.com/read/mbsrvrmmhrwr)


### Demonstration ###

For a run-through of the changes made since Demo 1, consult the following demonstration recording.

* :tv: [Demonstration 2](https://drive.google.com/drive/folders/1akAs5YBSMEl8_UUb5O6xQeK6xQavhJ_Z?usp=sharing)

## + Version 1.0.0 | <b style="color:teal">Demo 1</b> standard
#### New Additions and Fixes: ####

- Added first upload and interpretation services.
- Added first Key Signature, Time Signature and Tempo Indication interpreters.
### Demonstration ###

For a run-through of the first working model, consult the following demonstration recording.

* :tv: [Demonstration 1](https://drive.google.com/file/d/1u3yPmhA9ue9AsGq7qqYW6PU98itxFXpb/view)


# Project Management Resources

---

Project boards concerning various facets of project development can be found at the following links:

* :open_book: [Administration](https://github.com/COS301-SE-2021/Midi-Interpretation/projects/1)
* :open_book: [Backend Development](https://github.com/COS301-SE-2021/Midi-Interpretation/projects/2)
* :open_book: [Frontend Development](https://github.com/COS301-SE-2021/Midi-Interpretation/projects/3)

# Credits #

---
Special thanks are made to the following projects:

- [**mido**](https://github.com/mido/mido) | an open-source Python library that handles MIDI messages, used to develop a comprehensive lookup of tick-to-event information.

For a more comprehensive list of credits, please consult the "about us" page of the React service.


# The NoXception Team

---

NoXception is a team of five third-year EBIT undergraduates from the University of
Pretoria with a determination to embody best practices in real-world software
engineering. We have the drive to engage in development projects which challenge us
on a professional basis and enrich us on a personal level.

![Alt text](documentation/images/noxcpetion.png?raw=true "The NoXception Team Logo")

We have achieved a working knowledge of industry standards in software development and are capable of applying
the skills we have learnt throughout our degree to pressing real-world concerns. Our
combined skills and interests also encompass a wealth of diverse fields outside of
computer science and software engineering such as statistics, pure mathematics, design
and music.

| **Team Member** | **Profiles** | **Skills/Interest**
| :-----: | :-----: | :-----: |
| ![Hendro Smit](https://dl.dropboxusercontent.com/s/p5cams3icm2iy7c/hendrosmit.png?dl=0 "Hendro Smit") <br/> Hendro Smit <br/> u17004609 | [GitHub](https://github.com/hendrosmit) <br/> [Profile Page](https://hendrosmit.github.io/) <br/> [LinkedIn](https://www.linkedin.com/in/hendro-smit-328ba720b/) <br/> | Project Management <br> C++ <br> C# <br> Java <br> Game Development <br> Writing|
| ![Claudio Teixiera](documentation/images/ctimg.jpg "Claudio Teixeira") <br/> Claudio Teixiera <br/> u19028581 | [GitHub](https://github.com/Claudio-Uni) <br/>  [LinkedIn](https://www.linkedin.com/in/claudio-teixeira-b9bb9820b/) <br/> | Business Analyst <br> Writing <br> Statistics <br> Exercising <br> |
| ![Adrian Rae](documentation/images/arimg.jpg "Adrian Rae") <br/> Adrian Rae <br/> u19004029 | [GitHub](https://github.com/Adrian-Rae-19004029) <br/> [LinkedIn](https://www.linkedin.com/in/adrian-rae-5796b31bb/ ) <br/> | Research <br> Tutoring <br> Music Performance <br> Creative Writing |
| ![Rearabetswe Maeko](https://i.ibb.co/gDW0kS1/Rea.jpg "Rearabetswe Maeko") <br/> Rearabetswe Maeko <br/> u18179802 | [GitHub](https://github.com/u18094024) <br/> [Profile Page]() <br/> [LinkedIn](https://www.linkedin.com/in/rea-maeko-0b5a4a20b/) <br/> | Web-Development <br> Sport <br> Learning new things! |
| ![Mbuso Shakoane](https://i.ibb.co/Jpp9Xgc/Issa-Me-adobespark.jpg "Mbuso Shakoane") <br/> Mbuso Shakoane <br/> u18094024 | [GitHub](https://github.com/u18094024) <br/> [Profile Page]() <br/> [LinkedIn](https://www.linkedin.com/in/mbuso-shakoane-049a4920b/) <br/> | Systems design <br> Java <br> c++ <br> Cryptocurrencies |
